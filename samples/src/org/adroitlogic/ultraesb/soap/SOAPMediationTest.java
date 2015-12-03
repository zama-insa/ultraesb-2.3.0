/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * @author asankha
 */
public class SOAPMediationTest extends UTestCase {

    public static Test suite() {
        return new UTestSetup(SOAPMediationTest.class, 203);
    }

    public void testSOAPCall() throws Exception {

        // test with a valid request
        HttpPost httppost = new HttpPost("http://localhost:8280/service/soap-proxy-1");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        // test with a malformed XML
        httppost = new HttpPost("http://localhost:8280/service/soap-proxy-1");
        httppost.setEntity(new StringEntity(malformedGetQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Validation Failed"));

        // test with a request violating schema
        httppost = new HttpPost("http://localhost:8280/service/soap-proxy-1");
        httppost.setEntity(new StringEntity(invalidQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Validation Failed"));

        // test with CBR
        httppost = new HttpPost("http://localhost:8280/service/soap-proxy-2");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        // test with CBR for a failure case
        httppost = new HttpPost("http://localhost:8280/service/soap-proxy-2");
        httppost.setEntity(new StringEntity(validQuoteRequestACP, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        String responseContent = EntityUtils.toString(response.getEntity());
        System.out.println("==>" + responseContent);
        Assert.assertTrue(responseContent.contains("Endpoint stockquote-err failed, with error code 101503 : Connect attempt to server failed"));
    }

    private static final String validQuoteRequestACP =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.services.samples/\">\n" +
        "   <soapenv:Body>\n" +
        "      <soap:getQuote>\n" +
        "         <request>\n" +
        "            <symbol>ACP</symbol>\n" +
        "         </request>\n" +
        "      </soap:getQuote>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";

    private static final String invalidQuoteRequest =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.services.samples/\">\n" +
        "   <soapenv:Body>\n" +
        "      <soap:getQuote>\n" +
        "         <request>\n" +
        "            <symbols>ADRT</symbols>\n" +  // <<-- this is violating the schema
        "         </request>\n" +
        "      </soap:getQuote>\n" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";

    private static final String malformedGetQuoteRequest =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.services.samples/\">\n" +
        "   <soapenv:Body>\n" +
        "      <soap:getQuote>\n" +
        "         <request>\n" +
        "            <symbol>ADRT</symbol>\n" +
        "         </request>\n" +
        "      </soap:getQuotes>\n" +  // <<-- this is not well formed
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";
}
