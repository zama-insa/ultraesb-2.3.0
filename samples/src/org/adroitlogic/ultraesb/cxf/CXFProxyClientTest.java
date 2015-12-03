/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.cxf;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import samples.services.cxf.common.Customer;
import samples.services.cxf.common.CustomerService;
import samples.services.cxf.server.TestServer;

public class CXFProxyClientTest extends UTestCase {

    private static final String BASE_SERVICE_URL = "http://localhost:8280";
    private static TestServer testServer;

    public static Test suite() {
        return new TestSetup(new TestSuite(CXFProxyClientTest.class)) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-218.xml"});
                testServer = new TestServer();
                testServer.start();
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                testServer.stop();
            }
        };
    }

    public void testProxyClientCalls() throws Exception {

        CustomerService proxy = JAXRSClientFactory.create(BASE_SERVICE_URL, CustomerService.class);

        assertEquals("John", proxy.getCustomer("123").getName());

        Customer customer = new Customer();
        customer.setName("Asankha");
        proxy.addCustomer(customer);

        assertEquals("Asankha", proxy.getCustomer("124").getName());

        HttpGet httpget = new HttpGet("http://localhost:8280/customerservice-proxy?_wadl");
        HttpResponse response = client.execute(httpget);
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains(
                "resources base=\"http://localhost:8280/customerservice"));
    }
}
