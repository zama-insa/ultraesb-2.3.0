/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.endpoints;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.MultiServerUTestSetup;
import org.adroitlogic.ultraesb.UTestCase;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class EndpointsTest extends UTestCase {

    static {
        System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");
    }

    public static Test suite() {
        return new MultiServerUTestSetup(EndpointsTest.class, 601, 9000, 9001, 9002, 9003, 9004);
    }

    /*public void testWeightedDistribution() throws Exception {

        int[] counts = new int[NUM_SERVERS];

        for (int i=0; i<100; i++) {
            HttpPost httppost = new HttpPost("http://localhost:8280/service/weighted-proxy");
            httppost.setEntity(new StringEntity(getQuoteRequest, "text/xml", "UTF-8"));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            response.getEntity().consumeContent();

            int p = Integer.parseInt(response.getFirstHeader("port").getValue()) - port;
            counts[p]++;
        }

        // distribution should be within +/- 5%
        Assert.assertTrue(Math.abs(counts[0]/100.0d - 1.0d/12.0d) < 0.10);
        Assert.assertTrue(Math.abs(counts[1]/100.0d - 1.0d/12.0d) < 0.10);
        Assert.assertTrue(Math.abs(counts[2]/100.0d - 2.0d/12.0d) < 0.10);
        Assert.assertTrue(Math.abs(counts[3]/100.0d - 4.0d/12.0d) < 0.10);
        Assert.assertTrue(Math.abs(counts[4]/100.0d - 4.0d/12.0d) < 0.10);
    }

    public void testRoundRobinDistribution() throws Exception {

        int[] counts = new int[NUM_SERVERS];

        for (int i=0; i<100; i++) {
            HttpPost httppost = new HttpPost("http://localhost:8280/service/rr-proxy");
            httppost.setEntity(new StringEntity(getQuoteRequest, "text/xml", "UTF-8"));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            response.getEntity().consumeContent();

            int p = Integer.parseInt(response.getFirstHeader("port").getValue()) - port;
            counts[p]++;
        }

        // distribution should be within +/- 5%
        Assert.assertTrue(Math.abs(counts[0]/100.0d - 0.2d) < 0.05);
        Assert.assertTrue(Math.abs(counts[1]/100.0d - 0.2d) < 0.05);
        Assert.assertTrue(Math.abs(counts[2]/100.0d - 0.2d) < 0.05);
        Assert.assertTrue(Math.abs(counts[3]/100.0d - 0.2d) < 0.05);
        Assert.assertTrue(Math.abs(counts[4]/100.0d - 0.2d) < 0.05);
    }

    public void testFailOverOnlyDistribution() throws Exception {

        int[] counts = new int[NUM_SERVERS];

        for (int i=0; i<100; i++) {
            HttpPost httppost = new HttpPost("http://localhost:8280/service/fo-proxy");
            httppost.setEntity(new StringEntity(getQuoteRequest, "text/xml", "UTF-8"));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            response.getEntity().consumeContent();

            int p = Integer.parseInt(response.getFirstHeader("port").getValue()) - port;
            counts[p]++;
        }

        Assert.assertTrue(counts[0] == 100);
        Assert.assertTrue(counts[1] == 0);
        Assert.assertTrue(counts[2] == 0);
        Assert.assertTrue(counts[3] == 0);
        Assert.assertTrue(counts[4] == 0);
    }

    public void testFailOverOnlyFailingOver() throws Exception {

        int[] counts = new int[NUM_SERVERS];
        // shutdown server 1 and 3
        server[0].stop();
        server[2].stop();

        for (int i=0; i<100; i++) {
            HttpPost httppost = new HttpPost("http://localhost:8280/service/fo-proxy");
            httppost.setEntity(new StringEntity(getQuoteRequest, "text/xml", "UTF-8"));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            response.getEntity().consumeContent();

            int p = Integer.parseInt(response.getFirstHeader("port").getValue()) - port;
            counts[p]++;
        }

        Assert.assertTrue(counts[0] == 0);
        Assert.assertTrue(counts[1] == 100);
        Assert.assertTrue(counts[2] == 0);
        Assert.assertTrue(counts[3] == 0);
        Assert.assertTrue(counts[4] == 0);

        // restart server 1 and 3
        server[0].start();
        server[2].start();
    }
*/
    public void testFailOverOnResponseValidation() throws Exception {

        HttpPost httppost = new HttpPost("http://localhost:8280/service/validate-response-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        // this time it will work, as the endpoint is a failover, and the first address is now suspended
        httppost = new HttpPost("http://localhost:8280/service/validate-response-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost("http://localhost:8280/service/validate-response-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        // ignoring the suspend error
        httppost = new HttpPost("http://localhost:8280/service/validate-response-proxy-ignore");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost("http://localhost:8280/service/validate-response-proxy-ignore");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
    }

//    public void testRoundRobinFailingOver() throws Exception {
//
//        int[] counts = new int[NUM_SERVERS];
//
//        for (int i=0; i<100; i++) {
//            HttpPost httppost = new HttpPost("http://localhost:8280/service/rr-proxy");
//            httppost.setEntity(new StringEntity(getQuoteRequest, "text/xml", "UTF-8"));
//            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
//            HttpResponse response = client.execute(httppost);
//            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
//            response.getEntity().consumeContent();
//
//            int p = Integer.parseInt(response.getFirstHeader("port").getValue()) - port;
//            counts[p]++;
//        }
//
//        // distribution should be within +/- 5%
//        Assert.assertTrue(Math.abs(counts[0]/100.0d - 0.2d) < 0.05);
//        Assert.assertTrue(Math.abs(counts[1]/100.0d - 0.2d) < 0.05);
//        Assert.assertTrue(Math.abs(counts[2]/100.0d - 0.2d) < 0.05);
//        Assert.assertTrue(Math.abs(counts[3]/100.0d - 0.2d) < 0.05);
//        Assert.assertTrue(Math.abs(counts[4]/100.0d - 0.2d) < 0.05);
//    }
}
