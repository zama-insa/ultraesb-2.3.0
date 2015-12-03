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
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

public class AddressTest extends UTestCase {

    private static final String INVALID_URL_WITH_ERR_HANDLER = "http://localhost:8280/service/invalid-url-with-err-handler";
    private static final String INVALID_PORT = "http://localhost:8280/service/invalid-port";
    private static final String INVALID_URL_NO_ERR_HANDLER = "http://localhost:8280/service/invalid-url-no-err-handler";
    private static final String INVALID_ADDRESSES_WITH_RR = "http://localhost:8280/service/invalid-addresses-rr";

    public static Test suite() {
        return new MultiServerUTestSetup(AddressTest.class, 604, 9000, 9001);
    }

    public void testRrAddressSleep() throws Exception {

        HttpPost httppost = new HttpPost("http://localhost:8280/service/rr-failover-timeout-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        httppost.addHeader("sleep", "10000");
        long s = System.nanoTime();
        logger.info("Sent request at : " + new Date());
        HttpResponse response = client.execute(httppost);

        logger.info("Got response at : " + new Date());
        Assert.assertEquals("failed", EntityUtils.toString(response.getEntity()));
        Assert.assertTrue((System.nanoTime() - s) > 4000000000L);
        Assert.assertTrue((System.nanoTime() - s) < 8000000000L);

        Assert.assertEquals("Invalid Url", sendRequestAndReturnResponse(INVALID_URL_WITH_ERR_HANDLER));
        Assert.assertEquals("Invalid Port", sendRequestAndReturnResponse(INVALID_PORT));
        Assert.assertEquals("failed", timeOutAction(INVALID_URL_NO_ERR_HANDLER));
        Assert.assertEquals("Invalid Port", sendRequestAndReturnResponse(INVALID_ADDRESSES_WITH_RR));
    }

    public String timeOutAction(String url) throws IOException {
        try {
            client.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            client.execute(httppost);
            fail("Unexpected behaviour");
            return null;
        } catch (SocketTimeoutException e) {
            return "failed";
        }
    }

    private String sendRequestAndReturnResponse(String url) throws Exception {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        return EntityUtils.toString(response.getEntity());
    }
}
