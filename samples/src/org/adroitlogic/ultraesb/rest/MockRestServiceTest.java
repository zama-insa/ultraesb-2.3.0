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
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class MockRestServiceTest extends UTestCase {

    private static final String urlPrefix = "http://localhost:8280/service/rest-mock?x=5&y=6";

    public static Test suite() {
        return new UTestSetup(MockRestServiceTest.class, 103);
    }

    public void testMockResponseAndAuth() throws Exception {

        client.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", 8280),
            new UsernamePasswordCredentials("asankha", "adroitlogic"));

        HttpGet httpget = new HttpGet(urlPrefix);
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String resp = EntityUtils.toString(response.getEntity());
        System.out.println("Response : " + resp);
        Assert.assertTrue(
            "<response><user>asankha</user><roles>[ROLE_MANAGER, ROLE_USER]</roles><method>GET</method><uri>/service/rest-mock?x=5&y=6</uri><query>{x=[5], y=[6]}</query></response>".equals(resp) ||
            "<response><user>asankha</user><roles>[ROLE_MANAGER, ROLE_USER]</roles><method>GET</method><uri>/service/rest-mock?x=5&y=6</uri><query>{y=[6], x=[5]}</query></response>".equals(resp));
    }
}
