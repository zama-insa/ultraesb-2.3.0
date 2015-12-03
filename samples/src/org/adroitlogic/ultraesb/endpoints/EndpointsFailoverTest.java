/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.endpoints;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.MultiServerUTestSetup;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;

/**
 * @author asankha
 */
public class EndpointsFailoverTest extends UTestCase {

    private static UTestSetup setup;

    private static final String RR_PROXY = "http://localhost:8280/service/rr-proxy";
    private static final String RR_FO_PROXY = "http://localhost:8280/service/rr-fo-proxy";
    private static final String RR_FO_SF_PROXY = "http://localhost:8280/service/rr-proxy-safe-retry";
    private static final String RR_FO_SF_IGNORE_PROXY = "http://localhost:8280/service/rr-proxy-safe-retry-ignore";

    public static Test suite() {
        setup = new MultiServerUTestSetup(EndpointsFailoverTest.class, 602, 9000, 9001, 9002, 9003, 9004);
        return setup;
    }

    public void testRoundRobinOnly() throws Exception {

        Assert.assertEquals(9000, sendRequest(RR_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_PROXY));
        Assert.assertEquals(9000, sendRequest(RR_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_PROXY));
        setup.stopServer();  // stop on port 9000
        Thread.sleep(1000);
        Assert.assertEquals("failed", sendRequestAndReturnResponse(RR_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_PROXY));
        setup.startServer(); // start again on port 9000
        Thread.sleep(2500);  // give time to recover from suspension
        Assert.assertEquals(9000, sendRequest(RR_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_PROXY));
        Assert.assertEquals(9000, sendRequest(RR_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_PROXY));
    }

    public void testRoundRobinWithFailOver() throws Exception {

        Assert.assertEquals(9000, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9000, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_PROXY));
        setup.stopServer();  // stop on port 9000
        Thread.sleep(1000);
        Assert.assertEquals(9001, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_PROXY));
        setup.startServer(); // start again on port 9000
        Thread.sleep(2500);  // give time to recover from suspension
        Assert.assertEquals(9000, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9000, sendRequest(RR_FO_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_PROXY));
    }

    public void testRoundRobinWithFailOverSafeRetry() throws Exception {

        Assert.assertEquals(9000, sendRequest(RR_FO_SF_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_SF_PROXY));
        Assert.assertEquals("failed", sendSleepRequestAndReturnResponse(RR_FO_SF_PROXY, 9000));
        Assert.assertEquals(9001, sendRequest(RR_FO_SF_PROXY));
        Assert.assertEquals("failed", sendSleepRequestAndReturnResponse(RR_FO_SF_PROXY, 9000));
        Assert.assertEquals(9001, sendRequest(RR_FO_SF_PROXY));
    }

    public void testRoundRobinWithFailOverSafeRetryIgnore() throws Exception {

        Assert.assertEquals(9000, sendRequest(RR_FO_SF_IGNORE_PROXY));
        Assert.assertEquals(9001, sendRequest(RR_FO_SF_IGNORE_PROXY));
        Assert.assertEquals(9001, sendSleepRequest(RR_FO_SF_IGNORE_PROXY, 9000));  // 9000's turn, but gets retried
        Thread.sleep(4000);     // let grace period expire, sure to make it suspend
        Assert.assertEquals(9001, sendSleepRequest(RR_FO_SF_IGNORE_PROXY, 9000));  // 9000's turn, but gets retried and 9000 gets suspended
        Thread.sleep(1000);
        Assert.assertEquals(9001, sendRequest(RR_FO_SF_IGNORE_PROXY)); // 9000 is now suspended
        Thread.sleep(4500);
        Assert.assertEquals(9000, sendRequest(RR_FO_SF_IGNORE_PROXY)); // 9000 should be back in action
        Assert.assertEquals(9001, sendRequest(RR_FO_SF_IGNORE_PROXY));
    }

    private int sendRequest(String url) throws Exception {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        final Header portHeader = response.getFirstHeader("port");
        if (portHeader == null) {
            logger.warn(Arrays.toString(response.getAllHeaders()));
            return -1;
        } else {
            return Integer.parseInt(portHeader.getValue());
        }
    }

    private int sendSleepRequest(String url, int port) throws Exception {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        httppost.addHeader("sleep", "3000");
        httppost.addHeader("port", Integer.toString(port));
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        return Integer.parseInt(response.getFirstHeader("port").getValue());
    }

    private String sendRequestAndReturnResponse(String url) throws Exception {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        return EntityUtils.toString(response.getEntity());
    }

    private String sendSleepRequestAndReturnResponse(String url, int port) throws Exception {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        httppost.addHeader("sleep", "3000");
        httppost.addHeader("port", Integer.toString(port));
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        return EntityUtils.toString(response.getEntity());
    }
}
