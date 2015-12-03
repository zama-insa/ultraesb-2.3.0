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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asankha
 */
public class XSLTTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/jax-ws-proxy";

    public static Test suite() {
        return new UTestSetup(XSLTTest.class, 205);
    }

    public void testXSLTCall() throws Exception {
        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(checkPriceRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseStr = EntityUtils.toString(response.getEntity());
        logger.info("Response : " + responseStr);

        Pattern pattern = Pattern.compile("Price>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(responseStr);

        Assert.assertTrue(matcher.find());
        logger.info("Price : " + matcher.group(1));
    }

    private static final String checkPriceRequest =
        "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.services.samples/\">\n" +
        "   <soapenv:Body>\n" +
        "      <m0:CheckPriceRequest xmlns:m0=\"http://soap.services.samples/\">\n" +
        "         <m0:Code>ADRT</m0:Code>\n" +
        "      </m0:CheckPriceRequest>" +
        "   </soapenv:Body>\n" +
        "</soapenv:Envelope>";

}
