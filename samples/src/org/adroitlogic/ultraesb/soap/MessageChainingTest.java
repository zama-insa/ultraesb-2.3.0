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
public class MessageChainingTest extends UTestCase {

    public static Test suite() {
        return new UTestSetup(MessageChainingTest.class, 209);
    }

    public void testChainingWithString() throws Exception {

        for (int i = 0; i < 100; i++) {
            HttpPost httppost = new HttpPost("http://localhost:8280/service/soap-proxy-string");
            httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            Pattern pattern = Pattern.compile("last>([0-9|\\.]*)<");
            Matcher matcher = pattern.matcher(EntityUtils.toString(response.getEntity()));

            Assert.assertTrue(matcher.find());
            Assert.assertTrue(Double.parseDouble(matcher.group(1)) > 100.00);
            logger.info("Last : " + matcher.group(1));
        }
    }

    public void testChainingWithFile() throws Exception {

        for (int i = 0; i < 100; i++) {
            HttpPost httppost = new HttpPost("http://localhost:8280/service/soap-proxy-file");
            httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
            httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
            HttpResponse response = client.execute(httppost);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

            Pattern pattern = Pattern.compile("last>([0-9|\\.]*)<");
            Matcher matcher = pattern.matcher(EntityUtils.toString(response.getEntity()));

            Assert.assertTrue(matcher.find());
            Assert.assertTrue(Double.parseDouble(matcher.group(1)) > 100.00);
            logger.info("Last : " + matcher.group(1));
        }
    }
}
