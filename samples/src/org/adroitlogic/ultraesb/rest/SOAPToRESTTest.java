/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

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
public class SOAPToRESTTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/rest-stockquote";

    public static Test suite() {
        return new UTestSetup(SOAPToRESTTest.class, 109);
    }

    public void testRESTcall() throws Exception {

        logger.debug("*** Get a new stock quote ***");
        HttpGet httpGet = new HttpGet(proxyUrl + "?symbol=ADRT");
        HttpResponse response = client.execute(httpGet);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("application/xml", response.getFirstHeader(BaseConstants.Headers.CONTENT_TYPE).getValue());

        String responseStr = EntityUtils.toString(response.getEntity());
        Pattern pattern = Pattern.compile("last>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(responseStr);

        Assert.assertTrue(matcher.find());
        logger.info("Last : " + matcher.group(1));
        Assert.assertTrue(!responseStr.contains("Envelope"));
    }
}
