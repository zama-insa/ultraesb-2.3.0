/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.json;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.LoggerFactory;
import org.apache.derby.drda.NetworkServerControl;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

public class JSONDataServiceTest extends UTestCase {

    private static final String employeeSvc = "http://localhost:8280/employees";
    private static final String addEmployeeUntyped = "http://localhost:8280/service/addEmployeeUntyped";
    private static final String addEmployeeTyped = "http://localhost:8280/service/addEmployeeTyped";
    private static NetworkServerControl networkServerControl = null;

    public static Test suite() {

        return new TestSetup(new TestSuite(JSONDataServiceTest.class)) {

            protected void setUp() throws Exception {
                startDerby();
                createTable();
                UltraServer.main(new String[] {"--confDir=conf", "--rootConf=samples/conf/ultra-sample-214.xml"});
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
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1529/../modules/sample/target/unittestdb;user=admin;password=admin;create=true");
                try {
                    con.prepareStatement("DROP TABLE EMPLOYEE").executeUpdate();
                } catch (SQLException ignore) {}

                try {
                    con.prepareStatement(CREATE_TABLE).executeUpdate();
                    con.prepareStatement("INSERT INTO EMPLOYEE(employeeName, department, division, age, salary, sex, permanent, address, dateOfBirth)\n" +
                        "VALUES('tom', 2, 3, 35, 34500.0, 'male', 0, 'milano', '1975-02-23')").executeUpdate();
                    con.prepareStatement("INSERT INTO EMPLOYEE(employeeName, department, division, age, salary, sex, permanent, address, dateOfBirth)\n" +
                        "VALUES('mary', 2, 4, 36, 4454.0, 'female', 1, 'tokyo', '1974-12-05')").executeUpdate();
                    con.prepareStatement("INSERT INTO EMPLOYEE(employeeName, department, division, age, salary, sex, permanent, address, dateOfBirth)\n" +
                        "VALUES('nancy', 2, 3, 39, 22342.0, 'male', 1, 'london', '1971-05-14')").executeUpdate();
                    con.prepareStatement("INSERT INTO EMPLOYEE(employeeName, department, division, age, salary, sex, permanent, address, dateOfBirth)\n" +
                        "VALUES('bill', 3, 4, 60, 44533.0, 'female', 1, 'dulles', '1950-04-16')").executeUpdate();
                    con.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
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

    public void testJSONDataServices() throws Exception {

        HttpGet httpGet = new HttpGet(employeeSvc);
        HttpResponse response = client.execute(httpGet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        logger.info("Response : " + EntityUtils.toString(response.getEntity()));

        httpGet = new HttpGet(employeeSvc + "/1");
        response = client.execute(httpGet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        logger.info("Response : " + EntityUtils.toString(response.getEntity()));

        HttpPost httpPost = new HttpPost(addEmployeeUntyped);
        httpPost.setEntity(new StringEntity(ADD_EMPLOYEE_1, APPLICATION_JSON));
        response = client.execute(httpPost);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httpPost = new HttpPost(addEmployeeTyped);
        httpPost.setEntity(new StringEntity(ADD_EMPLOYEE_2, APPLICATION_JSON));
        response = client.execute(httpPost);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
    }

    private static final String CREATE_TABLE =
        "CREATE TABLE EMPLOYEE ( \n" +
            "\temployeeId  \tINTEGER GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1) NOT NULL,\n" +
            "\temployeeName\tVARCHAR(30),\n" +
            "\tage         \tINTEGER,\n" +
            "\tdepartment  \tINTEGER,\n" +
            "\tdivision    \tINTEGER,\n" +
            "\tsalary      \tDOUBLE,\n" +
            "\tsex         \tVARCHAR(6),\n" +
            "\tpermanent   \tSMALLINT,\n" +
            "\taddress     \tVARCHAR(60),  \n" +
            "\tdateOfBirth \tDATE\n" +
            "\t)";

    private static final String ADD_EMPLOYEE_1 = "{\"dateOfBirth\":\"1982-12-22\",\"" +
        "address\":\"washington\",\"employeeName\":\"jason\",\"salary\":34600.40,\"permanent\":true}";

    private static final String ADD_EMPLOYEE_2 = "{\"dateOfBirth\":\"1989-04-12\",\"" +
        "address\":\"westchester\",\"employeeName\":\"thomas\",\"salary\":54200.60,\"permanent\":false}";
}
