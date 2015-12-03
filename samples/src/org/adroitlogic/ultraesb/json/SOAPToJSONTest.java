/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.json;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.api.transport.BaseConstants;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asankha
 */
public class SOAPToJSONTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/json-stockquote";

    public static Test suite() {
        return new UTestSetup(SOAPToJSONTest.class, 112);
    }

    public void testRESTCallWithSAXStreaming() throws Exception {

        logger.debug("*** Get a new stock quote ***");
        HttpGet httpGet = new HttpGet(proxyUrl+"-1" + "?symbol=ADRT");
        HttpResponse response = client.execute(httpGet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(BaseConstants.ContentType.APPLICATION_JSON,
                response.getFirstHeader(BaseConstants.Headers.CONTENT_TYPE).getValue());

        String responseStr = EntityUtils.toString(response.getEntity());
        Pattern pattern = Pattern.compile("\"\\$\":\"([0-9|\\.]*)\"}");
        Matcher matcher = pattern.matcher(responseStr);

        Assert.assertTrue(matcher.find());
        logger.info("Last : " + matcher.group(1));
        Assert.assertTrue(!responseStr.contains("Envelope"));
    }

    public void testRESTCallWithXSLT() throws Exception {

        logger.debug("*** Get a new stock quote ***");
        HttpGet httpGet = new HttpGet(proxyUrl+"-2" + "?symbol=ADRT");
        HttpResponse response = client.execute(httpGet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals(BaseConstants.ContentType.APPLICATION_JSON,
                response.getFirstHeader(BaseConstants.Headers.CONTENT_TYPE).getValue());

        String responseStr = EntityUtils.toString(response.getEntity());
        Pattern pattern = Pattern.compile("\"last\":([0-9|\\.]*),");
        Matcher matcher = pattern.matcher(responseStr);

        Assert.assertTrue(matcher.find());
        logger.info("Last : " + matcher.group(1));
        Assert.assertTrue(!responseStr.contains("Envelope"));
    }
}
