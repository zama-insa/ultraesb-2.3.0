/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.core.helper.XMLFeatures;
import org.apache.axis.MessageContext;
import org.apache.axis.client.AxisClient;
import org.apache.axis.configuration.NullProvider;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ws.axis.security.WSDoAllSender;
import org.apache.ws.security.handler.WSHandlerConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asankha
 */
public class WSSecTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/ws-sec-proxy-certs";

    public static Test suite() {
        return new UTestSetup(WSSecTest.class, 204);
    }

    public void testSOAPCall() throws Exception {

        org.apache.axis.Message axisMsg = new org.apache.axis.Message(new ByteArrayInputStream(getQuoteRequest.getBytes()));
        MessageContext msgContext = new MessageContext(new AxisClient(new NullProvider()));
        msgContext.setCurrentMessage(axisMsg);

        WSDoAllSender doAllSender = new WSDoAllSender();
        Hashtable<String, String> opts = new Hashtable<String, String>();
        opts.put(WSHandlerConstants.ACTION, WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.ENCRYPT);
        opts.put(WSHandlerConstants.USER, "alice");
        opts.put(WSHandlerConstants.ENCRYPTION_USER, "bob");
        opts.put(WSHandlerConstants.PW_CALLBACK_CLASS, "org.adroitlogic.ultraesb.soap.PWCallback");
        opts.put(WSHandlerConstants.SIG_PROP_FILE, "sign.properties");
        opts.put(WSHandlerConstants.ENC_PROP_FILE, "enc.properties");
        doAllSender.setOptions(opts);

        doAllSender.invoke(msgContext);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLFeatures.getInstance().serialize(msgContext.getCurrentMessage().getSOAPEnvelope().getAsDocument(), baos);

        HttpPost httppost = new HttpPost(proxyUrl);
        httppost.setEntity(new StringEntity(baos.toString(), TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Pattern pattern = Pattern.compile("last>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(EntityUtils.toString(response.getEntity()));

        Assert.assertTrue(matcher.find());
        logger.info("Last : " + matcher.group(1));
    }
}
