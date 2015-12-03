/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class WebProxyTest extends UTestCase {

    public static Test suite() {
        return new UTestSetup(WebProxyTest.class, 102);
    }

    public void testProxiedWebPagesAndQueryReWrite() throws Exception {
        HttpGet httpget = new HttpGet("http://localhost:8280/service/HtmlServlet");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("<h1>Hello World</h1>"));

        //test parameter re-writing
        httpget = new HttpGet("http://localhost:8281/vpath?x=1&y=7&z=6&x=4");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains(
                "Parameters<br/>key: b values: 7<br/>key: c values: 6<br/>key: a values: 4"));

        //test parameter re-writing
        httpget = new HttpGet("http://localhost:8281/vpathgroovy?x=6&y=5&z=4&x=3");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains(
                "Parameters<br/>key: b values: 5<br/>key: c values: 4<br/>key: a values: 3"));
    }
}
