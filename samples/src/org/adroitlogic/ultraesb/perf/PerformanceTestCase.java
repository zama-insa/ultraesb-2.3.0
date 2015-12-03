/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.perf;

import org.adroitlogic.ultraesb.UTestCase;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * @author asankha
 */
public class PerformanceTestCase extends UTestCase {

    private volatile long nanoDirect = 0;
    private volatile long nanoCBR = 0;
    private volatile long nanoXSLT = 0;
    private volatile long nanoSec = 0;

    static {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 8280, PlainSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        cm.setMaxTotal(3000);
        cm.setDefaultMaxPerRoute(3000);
        HttpHost localhost = new HttpHost("localhost", 8280);
        cm.setMaxPerRoute(new HttpRoute(localhost), 2560);
    }

    public PerformanceTestCase(String name) throws Exception  {
        super(name);
    }

    public void testDirect() throws Exception {

        long start = System.nanoTime();
        HttpPost httppost = new HttpPost("http://localhost:8280/service/DirectProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.1");
        FileEntity entity = new FileEntity(new File("samples/resources/requests/500B_buyStocks.xml"), TEXT_XML);
        httppost.setEntity(entity);
        HttpResponse response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost("http://localhost:8280/service/DirectProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.100");
        entity = new FileEntity(new File("samples/resources/requests/100K_buyStocks.xml"), TEXT_XML);
        httppost.setEntity(entity);
        response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        nanoDirect += (System.nanoTime() - start);
    }

    public void testCBR() throws Exception {

        long start = System.nanoTime();
        HttpPost httppost = new HttpPost("http://localhost:8280/service/CBRProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.1");
        FileEntity entity = new FileEntity(new File("samples/resources/requests/500B_buyStocks.xml"), TEXT_XML);
        httppost.setEntity(entity);
        HttpResponse response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost("http://localhost:8280/service/CBRProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.100");
        entity = new FileEntity(new File("samples/resources/requests/100K_buyStocks.xml"), TEXT_XML);
        httppost.setEntity(entity);
        response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        nanoCBR += (System.nanoTime() - start);
    }

    public void testXSLT() throws Exception {

        long start = System.nanoTime();
        HttpPost httppost = new HttpPost("http://localhost:8280/service/XSLTProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.1");
        FileEntity entity = new FileEntity(new File("samples/resources/requests/500B_buyStocks.xml"), TEXT_XML);
        httppost.setEntity(entity);
        HttpResponse response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost("http://localhost:8280/service/XSLTProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.100");
        entity = new FileEntity(new File("samples/resources/requests/100K_buyStocks.xml"), TEXT_XML);
        httppost.setEntity(entity);
        response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        nanoXSLT += (System.nanoTime() - start);
    }

    public void testSecure() throws Exception {

        long start = System.nanoTime();
        HttpPost httppost = new HttpPost("http://localhost:8280/service/SecureProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.1");
        FileEntity entity = new FileEntity(new File("samples/resources/requests/500B_buyStocks_secure.xml"), TEXT_XML);
        httppost.setEntity(entity);
        HttpResponse response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        httppost = new HttpPost("http://localhost:8280/service/SecureProxy");
        httppost.addHeader("SOAPAction", "urn:buyStocks.100");
        entity = new FileEntity(new File("samples/resources/requests/100K_buyStocks_secure.xml"), TEXT_XML);
        httppost.setEntity(entity);
        response = client.execute(httppost);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        nanoSec += (System.nanoTime() - start);
    }

    public long getNanoDirect() {
        return nanoDirect;
    }

    public long getNanoCBR() {
        return nanoCBR;
    }

    public long getNanoXSLT() {
        return nanoXSLT;
    }

    public long getNanoSec() {
        return nanoSec;
    }
}
