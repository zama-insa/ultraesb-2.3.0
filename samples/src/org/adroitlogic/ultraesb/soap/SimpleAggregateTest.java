/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.api.transport.BaseConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import samples.services.soap.SimpleStockQuoteService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asankha
 */
public class SimpleAggregateTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/aggregate-proxy";

    public static Test suite() {
        return new UTestSetup(SimpleAggregateTest.class, 210);
    }

    public void testAggregatedCall() throws Exception {

        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        httppost.addHeader(BaseConstants.Headers.CONTENT_TYPE, BaseConstants.ContentType.TEXT_XML);
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String responseStr = EntityUtils.toString(response.getEntity());
        System.out.println("Response : " + responseStr);

        Pattern pattern = Pattern.compile("last xmlns=\"\">([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(responseStr);

        Assert.assertTrue(matcher.find());
        String val = matcher.group(1);
        logger.info("Minimum of Last trading price: " + val);
        
        Assert.assertEquals(SimpleStockQuoteService.min, Double.parseDouble(val));
    }
}
