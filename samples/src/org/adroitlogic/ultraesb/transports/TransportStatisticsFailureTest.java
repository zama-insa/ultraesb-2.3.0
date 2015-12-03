/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.transports;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.ultraesb.jmx.JMXConstants;
import org.adroitlogic.ultraesb.jmx.core.TransportManagementMXBean;
import org.adroitlogic.ultraesb.jmx.view.TransportView;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import samples.services.http.ErrorService;

import javax.management.JMX;
import javax.management.ObjectName;

public class TransportStatisticsFailureTest extends UTestCase {

    private static ErrorService errorService = new ErrorService();

    public static Test suite() {
        return new TestSetup(new TestSuite(TransportStatisticsFailureTest.class)) {

            protected void setUp() throws Exception {
                errorService.start();
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-606.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                errorService.stop();
            }
        };
    }

    /**
     * Tests for Transport failures,
     * Transport Listener - faults sending, faults receiving, timeout sending, timeout receiving
     * Transport Sender - faults sending, faults receiving, timeout sending, timeout receiving
     */
    public void testTransportFailuresTimeouts() throws Exception {
        errorService.start();
        TransportManagementMXBean transport = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_TRANSPORTS), TransportManagementMXBean.class);
        transport.resetListenerStatistics("http-8280");
        transport.resetSenderStatistics("http-sender");

        TransportView listener;
        TransportView sender;

        HttpPost httppost = new HttpPost("http://localhost:8280/service/error-echo-service?call=1");
        httppost.setEntity(new StringEntity("Hello World", "UTF-8"));
        httppost.addHeader("sleep-read", "3000");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        listener = transport.getTransportListener("http-8280");
        sender = transport.getTransportSender("http-sender");
        Assert.assertEquals(1, listener.getMessagesReceived());
        Assert.assertEquals(1, listener.getMessagesSent());
        Assert.assertEquals(0, listener.getFaultsSending());
        Assert.assertEquals(0, listener.getFaultsReceiving());
        Assert.assertEquals(0, sender.getMessagesReceived());
        Assert.assertEquals(0, sender.getMessagesSent());
        Assert.assertEquals(1, sender.getFaultsSending());
        Assert.assertEquals(0, sender.getFaultsReceiving());

        httppost = new HttpPost("http://localhost:8280/service/error-echo-service?call=2");
        httppost.setEntity(new StringEntity("Hello World", "UTF-8"));
        httppost.addHeader("close-write", "true");
        response = client.execute(httppost);
        Assert.assertEquals(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        listener = transport.getTransportListener("http-8280");
        sender = transport.getTransportSender("http-sender");
        Assert.assertEquals(2, listener.getMessagesReceived());
        Assert.assertEquals(2, listener.getMessagesSent());
        Assert.assertEquals(0, listener.getFaultsSending());
        Assert.assertEquals(0, listener.getFaultsReceiving());
        Assert.assertEquals(0, sender.getMessagesReceived());
        Assert.assertEquals(0, sender.getMessagesSent());
        Assert.assertEquals(1, sender.getFaultsSending());
        Assert.assertEquals(1, sender.getFaultsReceiving());
    }

    /**
     * Tests for Transport failures,
     * Transport Listener - faults sending, faults receiving, timeout sending, timeout receiving
     * Transport Sender - faults sending, faults receiving, timeout sending, timeout receiving
     */
    public void testTransportFailures() throws Exception {

        TransportManagementMXBean transport = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_TRANSPORTS), TransportManagementMXBean.class);
        transport.resetListenerStatistics("http-8280");
        transport.resetSenderStatistics("http-sender");

        TransportView listener;
        TransportView sender;

        HttpPost httppost = new HttpPost("http://localhost:8280/service/error-echo-service?call=1");
        httppost.setEntity(new StringEntity("Hello World", "UTF-8"));
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(org.apache.http.HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        listener = transport.getTransportListener("http-8280");
        sender = transport.getTransportSender("http-sender");
        Assert.assertEquals(1, listener.getMessagesReceived());
        Assert.assertEquals(1, listener.getMessagesSent());
        Assert.assertEquals(0, listener.getFaultsSending());
        Assert.assertEquals(0, listener.getFaultsReceiving());
        Assert.assertEquals(1, sender.getMessagesReceived());
        Assert.assertEquals(1, sender.getMessagesSent());
        Assert.assertEquals(0, sender.getFaultsSending());
        Assert.assertEquals(0, sender.getFaultsReceiving());

        errorService.stop();
        Thread.sleep(2000);

        httppost = new HttpPost("http://localhost:8280/service/error-echo-service?call=2");
        httppost.setEntity(new StringEntity("Hello World", "UTF-8"));
        response = client.execute(httppost);
        Assert.assertEquals(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        listener = transport.getTransportListener("http-8280");
        sender = transport.getTransportSender("http-sender");
        Assert.assertEquals(2, listener.getMessagesReceived());
        Assert.assertEquals(2, listener.getMessagesSent());
        Assert.assertEquals(0, listener.getFaultsSending());
        Assert.assertEquals(0, listener.getFaultsReceiving());
        Assert.assertEquals(1, sender.getMessagesReceived());
        Assert.assertEquals(1, sender.getMessagesSent());
        Assert.assertEquals(1, sender.getFaultsSending());
        Assert.assertEquals(0, sender.getFaultsReceiving());

        // start service and let address recover
        errorService.start();
        // give a second for address to become ready for retry
        Thread.sleep(1200);

        httppost = new HttpPost("http://localhost:8280/service/error-echo-service?call=3");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(org.apache.http.HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        listener = transport.getTransportListener("http-8280");
        sender = transport.getTransportSender("http-sender");
        Assert.assertEquals(3, listener.getMessagesReceived());
        Assert.assertEquals(3, listener.getMessagesSent());
        Assert.assertEquals(0, listener.getFaultsSending());
        Assert.assertEquals(0, listener.getFaultsReceiving());
        Assert.assertEquals(2, sender.getMessagesReceived());
        Assert.assertEquals(2, sender.getMessagesSent());
        Assert.assertEquals(1, sender.getFaultsSending());
        Assert.assertEquals(0, sender.getFaultsReceiving());

        httppost = new HttpPost("http://localhost:8280/service/error-echo-service?call=4");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        httppost.addHeader("close-read", "true");
        response = client.execute(httppost);
        Assert.assertEquals(org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        listener = transport.getTransportListener("http-8280");
        sender = transport.getTransportSender("http-sender");
        Assert.assertEquals(4, listener.getMessagesReceived());
        Assert.assertEquals(4, listener.getMessagesSent());
        Assert.assertEquals(0, listener.getFaultsSending());
        Assert.assertEquals(0, listener.getFaultsReceiving());
        Assert.assertEquals(2, sender.getMessagesReceived());
        Assert.assertEquals(2, sender.getMessagesSent());
        Assert.assertEquals(2, sender.getFaultsSending());
        Assert.assertEquals(0, sender.getFaultsReceiving());
    }
}
