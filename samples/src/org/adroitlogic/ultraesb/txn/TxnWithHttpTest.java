/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.txn;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.Logger;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import samples.services.JettyUtils;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author asankha
 */
public class TxnWithHttpTest extends UTestCase {

    private static final Logger logger = LoggerFactory.getLogger(TxnWithHttpTest.class);

    private static final String proxyUrl = "http://localhost:8280/service/txn-proxy";
    private static DefaultHttpClient client = new DefaultHttpClient();
    private static Server server = null;
    private static NetworkServerControl networkServerControl = null;

    public static Test suite() {
        return new TestSetup(new TestSuite(TxnWithHttpTest.class)) {
            protected void setUp() throws Exception {

                server = new Server(9000);
                server.setHandler(JettyUtils.sampleWebAppContext());
                server.start();

                startDerby();
                createTable();
                UltraServer.main(new String[] {"--confDir=conf", "--rootConf=samples/conf/ultra-sample-105.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                if (server != null) {
                    server.stop();
                }
                stopDerby();
            }

            private void startDerby() throws Exception {
                networkServerControl = new NetworkServerControl(InetAddress.getByName("localhost"), 1529);
                networkServerControl.start(null);

                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    try {
                        networkServerControl.ping();
                        Properties properties = networkServerControl.getCurrentProperties();
                        String host = properties.getProperty("derby.drda.host");
                        int port = Integer.parseInt(properties.getProperty("derby.drda.portNumber"));
                        logger.info("Database server started on " + host + " over port " + port);
                        return;
                    } catch (Exception ignore) {}
                }
            }

            private void createTable() throws Exception {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1529/../modules/sample/target/unittestdb;create=true");
                try {
                    con.prepareStatement("DROP TABLE quotes").executeUpdate();
                } catch (SQLException ignore) {}

                con.prepareStatement("CREATE TABLE quotes (symbol VARCHAR(10))").executeUpdate();
                con.close();
            }

            private void stopDerby() throws Exception {
                if (networkServerControl != null) {
                    networkServerControl.shutdown();
                    networkServerControl = null;
                    logger.info("Embedded Derby database server shutdown");
                }
                File derbyLog = new File("derby.log");
                if (derbyLog.exists()) {
                    if (!derbyLog.delete()) {
                        derbyLog.deleteOnExit();
                    }
                }
            }
        };
    }

    public void testTransactions() throws Exception {

        assertRowCount(0);
        
        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(getRequestFor("FAIL"), TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        assertRowCount(0);

        httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(getRequestFor("ADRT"), TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        assertRowCount(1);
    }

    private void assertRowCount(int expected) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1529/../modules/sample/target/unittestdb");
        ResultSet rs = con.prepareStatement("SELECT COUNT(*) AS rowcount FROM quotes").executeQuery();
        rs.next();
        System.out.println("Row count : " + rs.getInt("rowcount"));
        Assert.assertEquals(rs.getInt("rowcount"), expected);
        con.close();
    }

    private String getRequestFor(String symbol) {
        return
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.services.samples/\">\n" +
            "   <soapenv:Body>\n" +
            "      <soap:getQuote>\n" +
            "         <request>\n" +
            "            <symbol>" + symbol + "</symbol>\n" +
            "         </request>\n" +
            "      </soap:getQuote>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";
    }
}
