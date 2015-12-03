/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.jaxb;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.ultraesb.api.Environment;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.mediation.SOAPSupport;
import org.adroitlogic.ultraesb.api.mediation.XMLSupport;
import org.adroitlogic.ultraesb.core.ConfigurationImpl;
import org.adroitlogic.ultraesb.core.MediationImpl;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.adroitlogic.ultraesb.core.ProxyService;
import org.adroitlogic.ultraesb.core.deployment.DeploymentUnit;
import org.adroitlogic.ultraesb.core.format.StringMessage;
import org.adroitlogic.ultraesb.core.helper.XMLFeatures;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import samples.services.soap.jaxws.GetQuoteResponse;

/**
 * @author asankha
 */
public class JAXBTest extends TestCase {

    private static final String INJECTED_WHITESPACE = " \n ";

    private static final String XML_PAYLOAD = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<ns2:getQuoteResponse xmlns:ns2=\"http://soap.services.samples/\"><return>" + INJECTED_WHITESPACE +
        "<change>-2.377363277751826</change><earnings>-8.809453205292357</earnings>" +
        "<high>-142.82347154103255</high><last>145.83625569281193</last><lastTradeTimestamp>" +
        "Thu Nov 24 16:42:56 IST 2011</lastTradeTimestamp><low>150.88045953717682</low><marketCap>" +
        "4.208799612206715E7</marketCap><name>ADRT Company</name><open>-142.45031748050025</open>" +
        "<peRatio>24.094296624746196</peRatio><percentageChange>-1.515057620693605</percentageChange>" +
        "<prevClose>156.91570045127727</prevClose><symbol>ADRT</symbol><volume>17763</volume></return>" +
        "</ns2:getQuoteResponse>";

    private static final String SOAP_11_PAYLOAD =
        "<S11:Envelope xmlns:S11=\"http://schemas.xmlsoap.org/soap/envelope/\"><S11:Body>" + INJECTED_WHITESPACE +
        "<ns2:getQuoteResponse xmlns:ns2=\"http://soap.services.samples/\"><return>" +
        "<change>-2.377363277751826</change><earnings>-8.809453205292357</earnings>" +
        "<high>-142.82347154103255</high><last>145.83625569281193</last><lastTradeTimestamp>" +
        "Thu Nov 24 16:42:56 IST 2011</lastTradeTimestamp><low>150.88045953717682</low><marketCap>" +
        "4.208799612206715E7</marketCap><name>ADRT Company</name><open>-142.45031748050025</open>" +
        "<peRatio>24.094296624746196</peRatio><percentageChange>-1.515057620693605</percentageChange>" +
        "<prevClose>156.91570045127727</prevClose><symbol>ADRT</symbol><volume>17763</volume></return>" +
        "</ns2:getQuoteResponse></S11:Body></S11:Envelope>";

    private static final String SOAP_12_PAYLOAD =
        "<S12:Envelope xmlns:S12=\"http://www.w3.org/2003/05/soap-envelope\"><S12:Body>" + INJECTED_WHITESPACE +
        "<ns2:getQuoteResponse xmlns:ns2=\"http://soap.services.samples/\"><return>" +
        "<change>-2.377363277751826</change><earnings>-8.809453205292357</earnings>" +
        "<high>-142.82347154103255</high><last>145.83625569281193</last><lastTradeTimestamp>" +
        "Thu Nov 24 16:42:56 IST 2011</lastTradeTimestamp><low>150.88045953717682</low><marketCap>" +
        "4.208799612206715E7</marketCap><name>ADRT Company</name><open>-142.45031748050025</open>" +
        "<peRatio>24.094296624746196</peRatio><percentageChange>-1.515057620693605</percentageChange>" +
        "<prevClose>156.91570045127727</prevClose><symbol>ADRT</symbol><volume>17763</volume></return>" +
        "</ns2:getQuoteResponse></S12:Body></S12:Envelope>";
    
    private static final String TEST_SINGLE_CLASS = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + INJECTED_WHITESPACE +
        "<ns2:msg xmlns:ns2=\"http://test.com/msg\"><id>uuid</id><subject>sonde</subject><date>30042012</date>" +
        "<type>alarm start</type><code>215</code></ns2:msg>";

    private ProxyService ps;

    public static Test suite() {
        return new UTestSetup(JAXBTest.class, 114);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ps = ServerManager.getInstance().getConfig().getRootDeploymentUnit().getProxyService("ps");
    }

    public void testSingleClass() throws Exception {
        MessageImpl msg = new MessageImpl(true, ps, "null");
        msg.setCurrentPayload(new StringMessage(XML_PAYLOAD));
        msg.setContentType("text/xml");
        final Mediation mediation = MediationImpl.getInstance();

        Msg message = new Msg("uuid", "sonde", "30042012", "alarm start", "215");
        mediation.getXMLSupport().serializeJAXBObjectAsXMLStream(msg, message);
        Assert.assertEquals(TEST_SINGLE_CLASS.replaceAll(INJECTED_WHITESPACE,""), mediation.readPayloadAsString(msg));
    }

    public void testXMLAndJAXB() throws Exception {
        MessageImpl msg = new MessageImpl(true, ps, "null");
        msg.setCurrentPayload(new StringMessage(XML_PAYLOAD));
        msg.setContentType("text/xml");
        final Mediation mediation = MediationImpl.getInstance();
        final XMLSupport xmlSupport = mediation.getXMLSupport();

        GetQuoteResponse gqr = xmlSupport.convertXMLToJAXBObject(msg, GetQuoteResponse.class);
        Assert.assertEquals(-2.377363277751826d, gqr.getReturn().getChange());
        Assert.assertEquals("ADRT", gqr.getReturn().getSymbol());

        xmlSupport.serializeJAXBObjectAsXMLStream(msg, gqr, "getQuoteResponse", "http://soap.services.samples/");
        Assert.assertEquals(XML_PAYLOAD.replaceAll(INJECTED_WHITESPACE,""), mediation.readPayloadAsString(msg));
    }

    public void testSOAP11AndJAXB() throws Exception {
        MessageImpl msg = new MessageImpl(true, ps, "null");
        msg.setCurrentPayload(new StringMessage(SOAP_11_PAYLOAD));
        msg.setContentType("text/xml");
        final Mediation mediation = MediationImpl.getInstance();
        final SOAPSupport soapSupport = mediation.getSOAPSupport();

        GetQuoteResponse gqr = soapSupport.convertSOAPToJAXBObject(msg, GetQuoteResponse.class);
        Assert.assertEquals(-2.377363277751826d, gqr.getReturn().getChange());
        Assert.assertEquals("ADRT", gqr.getReturn().getSymbol());

        soapSupport.serializeJAXBObjectAsSOAP11Stream(msg, gqr, "getQuoteResponse", "http://soap.services.samples/");
        Assert.assertEquals(SOAP_11_PAYLOAD.replaceAll(INJECTED_WHITESPACE,""), mediation.readPayloadAsString(msg));
    }

    public void testSOAP12AndJAXB() throws Exception {
        MessageImpl msg = new MessageImpl(true, ps, "null");
        msg.setCurrentPayload(new StringMessage(SOAP_12_PAYLOAD));
        msg.setContentType("application/soap+xml");
        final Mediation mediation = MediationImpl.getInstance();
        final SOAPSupport soapSupport = mediation.getSOAPSupport();

        GetQuoteResponse gqr = soapSupport.convertSOAPToJAXBObject(msg, GetQuoteResponse.class);
        Assert.assertEquals(-2.377363277751826d, gqr.getReturn().getChange());
        Assert.assertEquals("ADRT", gqr.getReturn().getSymbol());

        soapSupport.serializeJAXBObjectAsSOAP12Stream(msg, gqr, "getQuoteResponse", "http://soap.services.samples/");
        Assert.assertEquals(SOAP_12_PAYLOAD.replaceAll(INJECTED_WHITESPACE,""), mediation.readPayloadAsString(msg));
    }
}
