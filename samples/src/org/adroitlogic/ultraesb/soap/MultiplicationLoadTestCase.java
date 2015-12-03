/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asankha
 */
@SuppressWarnings({"deprecation"})
public class MultiplicationLoadTestCase extends TestCase {

    private final String proxyUrl;
    private static HttpClient client = null;
    private static volatile int value1 = 1;
    private static volatile int value2 = 1;

    static {
        HttpParams params = new BasicHttpParams();
        // Increase max total connection to 200
        ConnManagerParams.setMaxTotalConnections(params, 200);
        // Increase default max connection per route to 20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
        // Increase max connections for localhost:8280 to 50
        HttpHost localhost = new HttpHost("locahost", 8280);
        connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(cm, params);
    }

    public MultiplicationLoadTestCase(String name) throws Exception  {
        super(name);
        if ("testMultiplication".equals(name)) {
            proxyUrl = "http://localhost:8280/service/multiply-proxy";
        } else {
            proxyUrl = "http://localhost:8280/service/echo-proxy";
        }
    }

    public void testMultiplication() throws Exception {
        int v1 =  value1++; //(int) getRandom(10000, 1.0, true);
        int v2 =  value2++; //(int) getRandom(10000, 1.0, true);

        StringBuilder sb = new StringBuilder(256);
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:jax=\"http://jaxws.services.samples/\"><soapenv:Body><jax:multiply><arg0>");
        sb.append(v1);
        sb.append("</arg0><arg1>");
        sb.append(v2);
        sb.append("</arg1></jax:multiply></soapenv:Body></soapenv:Envelope>");

        HttpPost httppost = new HttpPost(proxyUrl);
        StringEntity entity = new StringEntity(sb.toString(), "text/xml", "UTF-8");
        //entity.setChunked(true);
        httppost.setEntity(entity);

        httppost.addHeader("SOAPAction", "\"\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseStr = EntityUtils.toString(response.getEntity());
        Pattern pattern = Pattern.compile("result>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(responseStr);
        Assert.assertTrue(matcher.find());
        long result = Long.parseLong(matcher.group(1));

        if (result != (v1*v2)) {
            System.out.println("@@@ v1 : " + v1 + " v2 : " + v2 + " response : " + matcher.group(1) +
                " answer : " + (v1*v2) + " full response : " + responseStr);
            Assert.fail("Expected : " + (v1*v2) + " but response was : " + result);
        }
     }

    public void testEcho() throws Exception {
        int v1 =  value1++; //(int) getRandom(10000, 1.0, true);
        int v2 =  value2++; //(int) getRandom(10000, 1.0, true);

        StringBuilder sb = new StringBuilder(256);
        sb.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:jax=\"http://jaxws.services.samples/\"><soapenv:Body><jax:multiply><arg0>");
        sb.append(v1);
        sb.append("</arg0><arg1>");
        sb.append(v2);
        sb.append("</arg1></jax:multiply></soapenv:Body></soapenv:Envelope>");

        HttpPost httppost = new HttpPost(proxyUrl);
        StringEntity entity = new StringEntity(sb.toString(), "text/xml", "UTF-8");
        //entity.setChunked(true);
        httppost.setEntity(entity);

        httppost.addHeader("SOAPAction", "\"\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseStr = EntityUtils.toString(response.getEntity());
        Pattern pattern = Pattern.compile("arg0>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(responseStr);
        Assert.assertTrue(matcher.find());
        int v = Integer.parseInt(matcher.group(1));

        if (v != v1) {
            System.out.println("@@@ v1 : " + v1 + " v2 : " + v2 + " response value1 : " + matcher.group(1) +
                " answer : " + v1 + " full response : " + responseStr);
            Assert.fail("Expected : " + v1 + " but response was : " + v);
        }
    }

    private static double getRandom(double base, double varience, boolean onlypositive) {
        double rand = Math.random();
        return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
            * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
    }
}
