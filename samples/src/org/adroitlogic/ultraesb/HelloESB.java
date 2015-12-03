/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb;

import junit.framework.Assert;
import junit.framework.Test;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class HelloESB extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/echo-proxy";

    public static Test suite() {
        return new UTestSetup(HelloESB.class);
    }

    public void testHttpCall() throws Exception {
        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        logger.info("Reply => " + EntityUtils.toString(response.getEntity()));
    }
}
