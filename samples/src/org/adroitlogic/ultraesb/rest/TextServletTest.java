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
public class TextServletTest extends UTestCase {

    public static Test suite() {
        return new UTestSetup(TextServletTest.class, 101);
    }

    public void testTextResponse() throws Exception {
        // create a new customer
        HttpGet httpget = new HttpGet("http://localhost:8280/service/text-proxy");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Plain text Hello World"));
    }
}
