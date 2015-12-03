/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.txn;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.derby.drda.NetworkServerControl;

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
public class TxnWithTimerProxyTest extends UTestCase {

    private static final Logger logger = LoggerFactory.getLogger(TxnWithTimerProxyTest.class);

    private static NetworkServerControl networkServerControl = null;
    public static volatile String symbol = "ADRT";

    public static Test suite() {
        return new TestSetup(new TestSuite(TxnWithTimerProxyTest.class)) {
            protected void setUp() throws Exception {

                startDerby();
                createTable();
                UltraServer.main(new String[] {"--confDir=conf", "--rootConf=samples/conf/ultra-sample-107.xml"});
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
        Thread.sleep(3000);
        assertRowCount(1);
        symbol = "FAIL";
        Thread.sleep(4000);
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
}
