/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author asankha
 */
public class CompressionTest extends org.adroitlogic.ultraesb.UTestCase {

    private static DefaultHttpClient client = new DefaultHttpClient();

    public static Test suite() {
        return new TestSetup(new TestSuite(CompressionTest.class)) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-113.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
            }
        };
    }

    public void testResponseCompression() throws Exception {

        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(
                final HttpResponse response,
                final HttpContext context) throws IOException {
                HttpEntity entity = response.getEntity();
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    HeaderElement[] codecs = ceheader.getElements();
                    for (int i = 0; i < codecs.length; i++) {
                        if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(
                                new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });

        HttpResponse response;
        HttpGet httpget;

        httpget = new HttpGet("http://localhost:8280/service/compressed1");
        httpget.addHeader("Accept-Encoding", "gzip");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(response.getFirstHeader(HTTP.CONTENT_ENCODING).getValue(), "gzip");
        Assert.assertEquals(readFileAsString("samples/resources/requests/500K_buyStocks_secure.xml"),
            EntityUtils.toString(response.getEntity()));

        httpget = new HttpGet("http://localhost:8280/service/compressed1");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getFirstHeader(HTTP.CONTENT_ENCODING));
        Assert.assertEquals(readFileAsString("samples/resources/requests/500K_buyStocks_secure.xml"),
            EntityUtils.toString(response.getEntity()));

        httpget = new HttpGet("http://localhost:8280/service/compressed2");
        httpget.addHeader("Accept-Encoding", "gzip");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getFirstHeader(HTTP.CONTENT_ENCODING));
        Assert.assertEquals(readFileAsString("samples/resources/requests/1K_buyStocks.xml"),
            EntityUtils.toString(response.getEntity()));

        httpget = new HttpGet("http://localhost:8280/service/compressed3");
        httpget.addHeader("Accept-Encoding", "gzip");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getFirstHeader(HTTP.CONTENT_ENCODING));
        Assert.assertEquals("Hello World",
            EntityUtils.toString(response.getEntity()));

        httpget = new HttpGet("http://localhost:8280/service/compressed4");
        httpget.addHeader("Accept-Encoding", "gzip");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(response.getFirstHeader(HTTP.CONTENT_ENCODING).getValue(), "gzip");
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<2048; i++) {
            sb.append("Hello again; ");
        }
        Assert.assertEquals(sb.toString(),
            EntityUtils.toString(response.getEntity()));

        // no compression, inDestination is a response address
        httpget = new HttpGet("http://localhost:8280/service/compressed5");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getFirstHeader(HTTP.CONTENT_ENCODING));
        Assert.assertEquals(readFileAsString("samples/resources/requests/500K_buyStocks_secure.xml"),
                EntityUtils.toString(response.getEntity()));

        // with compression, inDestination is a response address
        httpget = new HttpGet("http://localhost:8280/service/compressed5");
        httpget.addHeader("Accept-Encoding", "gzip");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(response.getFirstHeader(HTTP.CONTENT_ENCODING).getValue(), "gzip");
        Assert.assertEquals(readFileAsString("samples/resources/requests/500K_buyStocks_secure.xml"),
                EntityUtils.toString(response.getEntity()));

        // no compression, sendToEndpoint is a response address
        httpget = new HttpGet("http://localhost:8280/service/compressed6");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getFirstHeader(HTTP.CONTENT_ENCODING));
        Assert.assertEquals(readFileAsString("samples/resources/requests/500K_buyStocks_secure.xml"),
                EntityUtils.toString(response.getEntity()));

        // with compression, sendToEndpoint is a response address
        httpget = new HttpGet("http://localhost:8280/service/compressed6");
        httpget.addHeader("Accept-Encoding", "gzip");
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(response.getFirstHeader(HTTP.CONTENT_ENCODING).getValue(), "gzip");
        Assert.assertEquals(readFileAsString("samples/resources/requests/500K_buyStocks_secure.xml"),
                EntityUtils.toString(response.getEntity()));
    }

    private static String readFileAsString(String path) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(path));
        String str;
        StringBuilder sb = new StringBuilder(20);
        while ((str = in.readLine()) != null) {
            sb.append(str).append("\n");
        }
        in.close();
        return sb.toString();
    }

    static class GzipDecompressingEntity extends HttpEntityWrapper {

        public GzipDecompressingEntity(final HttpEntity entity) {
            super(entity);
        }

        @Override
        public InputStream getContent()
            throws IOException, IllegalStateException {
            // the wrapped entity's getContent() decides about repeatability
            InputStream wrappedin = wrappedEntity.getContent();
            return new GZIPInputStream(wrappedin);
        }

        @Override
        public long getContentLength() {
            // length of ungzipped content is not known
            return -1;
        }
    }
}
