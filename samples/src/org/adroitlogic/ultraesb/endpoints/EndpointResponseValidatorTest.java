/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.endpoints;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.core.SampleResponseValidator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Tests the use of response validator class in an endpoint, sample 607
 *
 * @author Ruwan
 * @since 2.0.0
 */
public class EndpointResponseValidatorTest extends UTestCase {

    static {
        System.setProperty("com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace", "false");
    }

    public static Test suite() {
        return new UTestSetup(EndpointResponseValidatorTest.class, 607);
    }

    public void testResponseValidation() throws Exception {

        HttpPost httppost = new HttpPost("http://localhost:8280/service/response-validate-proxy");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());
        assertEquals(1, SampleResponseValidator.validatedRespCount.get());
    }
}
