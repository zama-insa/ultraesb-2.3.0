/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class AuthenticationTest extends UTestCase {

    private static final String RESPONSE =
        "<response><user>asankha</user><roles>[ROLE_MANAGER, ROLE_USER]</roles><method>GET</method><uri>/service/rest-mock?hello=world</uri><query>{hello=[world]}</query></response>";
    private static DefaultHttpClient client = new DefaultHttpClient();

    public static Test suite() {
        return new TestSetup(new TestSuite(AuthenticationTest.class)) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-110.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
            }
        };
    }

    public void testDirectWithBasicAuth() throws Exception {

        client.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", 8281),
            new UsernamePasswordCredentials("asankha", "adroitlogic"));

        HttpGet httpget = new HttpGet("http://localhost:8281/service/rest-mock?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testDirectWithDigestAuth() throws Exception {

        client.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", 8282),
            new UsernamePasswordCredentials("asankha", "adroitlogic"));

        HttpGet httpget = new HttpGet("http://localhost:8282/service/rest-mock?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testPreemptiveBasicAuth1() throws Exception {

        HttpGet httpget = new HttpGet("http://localhost:8280/service/preemptive-basic-auth-proxy-1?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testPreemptiveBasicAuth2() throws Exception {

        HttpGet httpget = new HttpGet("http://localhost:8280/service/preemptive-basic-auth-proxy-2?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testPreemptiveAmazonS3Auth() throws Exception {

        HttpPut httpput = new HttpPut("http://localhost:8280/service/s3-auth-proxy/test.txt");
        httpput.setEntity(new StringEntity("Hello World"));
        HttpResponse response = client.execute(httpput);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        if (response.getEntity() != null) {
            EntityUtils.consume(response.getEntity());
        }

        HttpGet httpget = new HttpGet("http://localhost:8280/service/s3-auth-proxy/test.txt");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Hello World", EntityUtils.toString(response.getEntity()));

        httpput = new HttpPut("http://localhost:8280/service/s3-auth-proxy/test.txt");
        httpput.setEntity(new StringEntity("UltraESB Rocks!"));
        response = client.execute(httpput);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        if (response.getEntity() != null) {
            EntityUtils.consume(response.getEntity());
        }

        httpget = new HttpGet("http://localhost:8280/service/s3-auth-proxy/test.txt");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("UltraESB Rocks!", EntityUtils.toString(response.getEntity()));

        // TODO: Due to some reason DELETE was failing with 405 method not allowed on 16/12/12, check the error
        /*HttpDelete httpdel = new HttpDelete("http://localhost:8280/service/s3-auth-proxy/test.txt");
        response = client.execute(httpdel);
        Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
        if (response.getEntity() != null) {
            EntityUtils.consume(response.getEntity());
        }*/

        /*httpget = new HttpGet("http://localhost:8280/service/s3-auth-proxy/test.txt");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
        if (response.getEntity() != null) {
            EntityUtils.consume(response.getEntity());
        }*/
    }

    public void testBasicAuthProxy() throws Exception {

        HttpGet httpget = new HttpGet("http://localhost:8280/service/basic-auth-proxy?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testDigestAuthProxy() throws Exception {

        HttpGet httpget = new HttpGet("http://localhost:8280/service/digest-auth-proxy?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testBasicAuthProxyWrongPassword() throws Exception {

        client.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", 8280),
            new UsernamePasswordCredentials("asankha", "adroitlogic"));

        HttpGet httpget = new HttpGet("http://localhost:8280/service/basic-auth-proxy-wrong?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }

    public void testDigestAuthProxyWrongPassword() throws Exception {

        client.getCredentialsProvider().setCredentials(
            new AuthScope("localhost", 8280),
            new UsernamePasswordCredentials("asankha", "adroitlogic"));

        HttpGet httpget = new HttpGet("http://localhost:8280/service/digest-auth-proxy-wrong?hello=world");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(RESPONSE, EntityUtils.toString(response.getEntity()));
    }
}
