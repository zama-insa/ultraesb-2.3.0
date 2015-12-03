/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.adroitlogic.ultraesb.api.transport.BaseConstants;
import org.apache.http.Consts;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
@SuppressWarnings({"deprecation"})
public class XSLTLoadTestCase extends TestCase {

    private static final String proxyUrl = "http://localhost:8280/service/xslt-load-test-proxy";
    private static HttpClient client = null;

    static {
        HttpParams params = new BasicHttpParams();
        // Increase max total connection to 200
        ConnManagerParams.setMaxTotalConnections(params, 200);
        // Increase default max connection per route to 20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
        // Increase max connections for localhost:8280 to 50
        HttpHost localhost = new HttpHost("localhost", 8280);
        connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);
        client = new DefaultHttpClient(cm, params);
    }

    public XSLTLoadTestCase(String name) throws Exception  {
        super(name);
    }

    public void testSimpleXSLT() throws Exception {
        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(getQuoteRequest,
                ContentType.create(BaseConstants.ContentType.TEXT_XML, Consts.UTF_8)));
        httppost.addHeader("SOAPAction", "\"urn:buyStocks\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
    }

    private static final String getQuoteRequest =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
        "<soapenv:Body>\n" +
        "<m:buyStocks xmlns:m=\"http://services.samples/xsd\">\n" +
        "<order><symbol>IBM</symbol><buyerID>asankha</buyerID><price>140.34</price><volume>2000</volume></order>\n" +
        "<order><symbol>MSFT</symbol><buyerID>ruwan</buyerID><price>23.56</price><volume>8030</volume></order>\n" +
        "<order><symbol>SUN</symbol><buyerID>indika</buyerID><price>14.56</price><volume>500</volume></order>\n" +
        "<order><symbol>GOOG</symbol><buyerID>chathura</buyerID><price>60.24</price><volume>40000</volume></order>\n" +
        "<order><symbol>IBM</symbol><buyerID>asankha</buyerID><price>140.34</price><volume>2000</volume></order>\n" +
        "<order><symbol>MSFT</symbol><buyerID>ruwan</buyerID><price>23.56</price><volume>803000</volume></order>\n" +
        "<order><symbol>SUN</symbol><buyerID>indika</buyerID><price>14.56</price><volume>50000</volume></order>\n" +
        "<order><symbol>GOOG</symbol><buyerID>saliya</buyerID><price>60.24</price><volume>400000</volume></order>\n" +
        "</m:buyStocks>\n" +
        "</soapenv:Body>\n" +
        "</soapenv:Envelope>";

}
