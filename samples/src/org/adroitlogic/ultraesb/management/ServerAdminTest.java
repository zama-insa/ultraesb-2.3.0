/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.management;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.api.management.*;
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
public class ServerAdminTest extends UTestCase {

    private static final String proxyUrl = "http://localhost:8280/service/dynamic-proxy-";

    public static Test suite() {
        return new UTestSetup(ServerAdminTest.class);
    }

    public void testDynamicProxyCreation() throws Exception {
        
        ServerAdmin sa = ServerAdminImpl.getInstance();

        ProxyServiceDefinition psDef = new ProxyServiceDefinition("dynamic-proxy-1");
        psDef.exposeOverTransport("http-8280", null);

        EndpointDefinition epDef = new EndpointDefinition();
        epDef.addAddress(new AddressDefinition("http://localhost:9000/service/SimpleStockQuoteService"));
        psDef.setInDestinationDefinition(epDef);

        epDef = new EndpointDefinition();
        epDef.addAddress(AddressDefinition.RESPONSE);
        psDef.setOutDestinationDefinition(epDef);

        GenericDeploymentUnitDefinition def = new GenericDeploymentUnitDefinition("dynamic-du");
        def.addProxyService(psDef);
        sa.addOrUpdateDeploymentUnit(def);

        HttpPost httppost = new HttpPost(proxyUrl + 1);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Pattern pattern = Pattern.compile("last>([0-9|\\.]*)<");
        Matcher matcher = pattern.matcher(EntityUtils.toString(response.getEntity()));

        Assert.assertTrue(matcher.find());
        logger.info("Last : " + matcher.group(1));

        psDef = new ProxyServiceDefinition("dynamic-proxy-2");
        psDef.exposeOverTransport("http-8280", null);

        SequenceDefinition seqDef = new SequenceDefinition("myinSequence");
        seqDef.defineFromJavaFragmentText("org.adroitlogic.ultraesb.api.transport.http.HttpConstants;",
            "mediation.setPayloadFromString(msg, \"<hello/>\");\n" +
            "mediation.sendResponse(msg, 200);");
        psDef.setInSequenceId("myinSequence");

        def = new GenericDeploymentUnitDefinition("dynamic2");
        def.addSequence(seqDef);
        def.addProxyService(psDef);
        sa.addOrUpdateDeploymentUnit(def);

        httppost = new HttpPost(proxyUrl + 2);
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Assert.assertEquals(EntityUtils.toString(response.getEntity()), "<hello/>");

        sa.unloadDeploymentUnit("dynamic2");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
    }
}
