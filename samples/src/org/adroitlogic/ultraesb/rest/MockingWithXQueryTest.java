/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author asankha
 */
public class MockingWithXQueryTest extends UTestCase {

    private static final Logger logger = LoggerFactory.getLogger(MockingWithXQueryTest.class);

    private static NetworkServerControl networkServerControl = null;
    public static volatile String symbol = "ADRT";
    private static HttpClient client = new DefaultHttpClient();

    public static Test suite() {
        return new TestSetup(new TestSuite(MockingWithXQueryTest.class)) {

            protected void setUp() throws Exception {
                startDerby();
                createAndPopulateTable();
                UltraServer.main(new String[] {"--confDir=conf", "--rootConf=samples/conf/ultra-sample-108.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
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

            private void createAndPopulateTable() throws Exception {
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1529/../modules/sample/target/unittestdb;create=true;", "admin", "admin");
                try {
                    con.prepareStatement("DROP TABLE SUBSCRIPTION").executeUpdate();
                } catch (SQLException ignore) {}

                con.prepareStatement("CREATE TABLE SUBSCRIPTION ( \n" +
                    "\tID       \tVARCHAR(25),\n" +
                    "\tSTARTDATE\tTIMESTAMP,\n" +
                    "\tENDDATE  \tTIMESTAMP,\n" +
                    "\tCATEGORY \tINTEGER,\n" +
                    "\tUSERNAME \tVARCHAR(25),\n" +
                    "\tETAG     \tVARCHAR(25) \n" +
                    "\t)").executeUpdate();

                con.prepareStatement("INSERT INTO SUBSCRIPTION(ID, STARTDATE, ENDDATE, CATEGORY, USERNAME, ETAG)\n" +
                    "  VALUES('12345', '2010-03-17 15:36:36.246', '2011-07-27 15:36:40.365', 3, 'john', '686897696a7c876b7e')").executeUpdate();
                con.prepareStatement("INSERT INTO SUBSCRIPTION(ID, STARTDATE, ENDDATE, CATEGORY, USERNAME, ETAG)\n" +
                    "  VALUES('67890', '2010-03-17 17:05:10.416', '2012-06-07 17:05:12.096', 4, 'jim', '34554232423432')").executeUpdate();

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

    public void testMockGet() throws Exception {

        HttpGet httpget = new HttpGet("http://localhost:8280/service/subscription-mock?id=12345");
        httpget.addHeader("If-None-Match", "686897696a7c876b7e");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatusLine().getStatusCode());

        httpget = new HttpGet("http://localhost:8280/service/subscription-mock?id=67890");
        httpget.addHeader("If-None-Match", "1234567890");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals("34554232423432", response.getFirstHeader("ETag").getValue());

        httpget = new HttpGet("http://localhost:8280/service/subscription-mock?id=67890");
        httpget.addHeader("If-None-Match", "1234567890");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals("34554232423432", response.getFirstHeader("ETag").getValue());

        httpget = new HttpGet("http://localhost:8280/service/subscription-mock?id=67890");
        httpget.addHeader("If-None-Match", "34554232423432");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_NOT_MODIFIED, response.getStatusLine().getStatusCode());

        HttpPost httppost = new HttpPost("http://localhost:8280/service/subscription-mock?id=12345");
        httppost.setEntity(new StringEntity(MSG, TEXT_XML));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseString = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(responseString.contains("currentBalance"));
        logger.info("Response : " + responseString);
    }

    private static final String MSG =
        "<m:updateSubscription xmlns:m=\"http://mock.samples/\">\n" +
        "  <m:id>12345</m:id>\n" +
        "  <m:username>12345</m:username>\n" +
        "  <m:amountPaid>220.00</m:amountPaid>\n" +
        "  <m:currency>GBP</m:currency>\n" +
        "  <m:dateOfPayment>16-03-2010</m:dateOfPayment>\n" +
        "</m:updateSubscription>";
}
