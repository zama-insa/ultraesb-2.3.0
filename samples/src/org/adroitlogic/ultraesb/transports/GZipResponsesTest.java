/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.transports;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Test case that tests GZipped responses for various message types
 *
 * @author Ruwan
 * @since 2.1.0
 */
public class GZipResponsesTest extends UTestCase {

    public static Test suite() {
        return new UTestSetup(GZipResponsesTest.class, 220);
    }

    public void testSOAPSecCall() throws Exception {

        HttpPost httppost = new HttpPost("http://localhost:8280/service/echo-sec-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String signedPayload = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(signedPayload.contains("wsse:Security"));
        Assert.assertTrue(signedPayload.contains("DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\""));

        HttpPost httppost2 = new HttpPost("http://localhost:8280/service/ws-sec-proxy");
        httppost2.setEntity(new StringEntity(signedPayload, TEXT_XML));
        httppost2.addHeader("SOAPAction", "\"urn:getQuote\"");
        httppost2.addHeader("Accept-Encoding", "gzip");
        HttpResponse response2 = client.execute(httppost2);
        Assert.assertEquals(HttpStatus.SC_OK, response2.getStatusLine().getStatusCode());
        Assert.assertEquals("gzip", response2.getFirstHeader("Content-Encoding").getValue());
        String signedResponse = unCompressResponse(response2);
        Assert.assertTrue(signedResponse.contains("wsse:Security"));
        Assert.assertTrue(signedResponse.contains("DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\""));
    }

    public void testSOAPJSONCall() throws Exception {
        invoke("http://localhost:8280/service/soap-json-proxy");
    }

    public void testRawJSONCall() throws Exception {
        invoke("http://localhost:8280/service/json-proxy");
    }

    public void testTextCall() throws Exception {
        invoke("http://localhost:8280/service/text-proxy");
    }

    public void testMinCompressionSize() throws Exception {
        HttpPost httppost = new HttpPost("http://localhost:8280/service/no-zip-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        httppost.addHeader("Accept-Encoding", "gzip");
        HttpResponse acceptZipResponse = client.execute(httppost);

        Assert.assertEquals(HttpStatus.SC_OK, acceptZipResponse.getStatusLine().getStatusCode());
        Assert.assertNull(acceptZipResponse.getFirstHeader("Content-Encoding"));
        Assert.assertEquals("NoZip", EntityUtils.toString(acceptZipResponse.getEntity()));
    }

    private void invoke(String url) throws Exception {
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String withoutZip = EntityUtils.toString(response.getEntity());

        httppost.addHeader("Accept-Encoding", "gzip");
        HttpResponse gzippedResponse = client.execute(httppost);

        Assert.assertEquals(HttpStatus.SC_OK, gzippedResponse.getStatusLine().getStatusCode());
        Assert.assertEquals("gzip", gzippedResponse.getFirstHeader("Content-Encoding").getValue());
        String unCompressed = unCompressResponse(gzippedResponse);
        Assert.assertEquals(withoutZip, unCompressed);
    }

    private String unCompressResponse(HttpResponse response) throws IOException {
        GZIPInputStream gzis = new GZIPInputStream(response.getEntity().getContent());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(gzis, baos);
        EntityUtils.consume(response.getEntity());
        return new String(baos.toByteArray());
    }
}
