/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.xacml;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class XACMLTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/soap-proxy";

    public static Test suite() {
        return new UTestSetup(XACMLTest.class, 216);
    }

    public void testDirectWithBasicAuth() throws Exception {

        client.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", 8280),
            new UsernamePasswordCredentials("asankha", "adroitlogic"));

        HttpPost httppost = new HttpPost(proxyUrl+"-1");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost(proxyUrl+"-2");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
    }
}
