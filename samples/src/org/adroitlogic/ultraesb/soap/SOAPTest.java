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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asankha
 */
public class SOAPTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/soap-proxy";

    public static Test suite() {
        return new UTestSetup(SOAPTest.class, 201);
    }

    public void testSOAPCall() throws Exception {

        logger.debug("*** Get a new stock quote ***");
        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Pattern pattern = Pattern.compile("last>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(EntityUtils.toString(response.getEntity()));
        
        Assert.assertTrue(matcher.find());
        logger.info("Last : " + matcher.group(1));

        HttpGet httpget = new HttpGet(proxyUrl + "?wsdl");
        response = client.execute(httpget);
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains(
                "address location=\"http://localhost:8280/service/soap-proxy"));

        httpget = new HttpGet(proxyUrl + "?xsd=1");
        response = client.execute(httpget);
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains(
                "<xs:element name=\"getFullQuote\" type=\"tns:getFullQuote\"/>"));
    }
}
