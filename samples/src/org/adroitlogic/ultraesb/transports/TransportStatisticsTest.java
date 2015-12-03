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
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import samples.services.http.MetricsService;

import javax.management.JMX;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.Random;

public class TransportStatisticsTest extends UTestCase {

    private static final String TEXT = "0123456789";
    private static final Random RANDOM = new Random(System.currentTimeMillis());
    private static MetricsService metricsService = new MetricsService();

    public static Test suite() {
        return new TestSetup(new TestSuite(TransportStatisticsTest.class)) {

            protected void setUp() throws Exception {
                metricsService.start();
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-605.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                metricsService.stop();
            }
        };
    }

    public void testListenerStatistics() throws Exception {

        TransportManagementMXBean transport = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_TRANSPORTS), TransportManagementMXBean.class);

        final Holder holder = new Holder();
        client.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse httpRequest, HttpContext context) throws IOException {
                ManagedClientConnection connAdaptor = (ManagedClientConnection) context.getAttribute(ExecutionContext.HTTP_CONNECTION);
                holder.setMetrics(connAdaptor.getMetrics());
            }
        });

        HttpPost httppost = new HttpPost("http://localhost:8280/service/metrics-echo-service?call=1");
        httppost.setEntity(new StringEntity(randomString(), "UTF-8"));
        HttpResponse response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        TransportView lView = transport.getTransportListener("http-8280");
        TransportView sView = transport.getTransportSender("http-sender");
        Assert.assertEquals(1, lView.getMessagesSent());
        Assert.assertEquals(1, lView.getMessagesReceived());
        Assert.assertEquals(holder.getMetrics().getSentBytesCount(), lView.getBytesReceived());
        Assert.assertEquals(holder.getMetrics().getReceivedBytesCount(), lView.getBytesSent());
        Assert.assertEquals(MetricsService.sent.get(), sView.getBytesReceived());
        Assert.assertEquals(MetricsService.received.get(), sView.getBytesSent());

        httppost = new HttpPost("http://localhost:8280/service/metrics-echo-service?call=2");
        httppost.setEntity(new StringEntity(randomString(), "UTF-8"));
        response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        lView = transport.getTransportListener("http-8280");
        sView = transport.getTransportSender("http-sender");
        Assert.assertEquals(2, lView.getMessagesReceived());
        Assert.assertEquals(2, lView.getMessagesSent());
        Assert.assertEquals(holder.getMetrics().getSentBytesCount(), lView.getBytesReceived());
        Assert.assertEquals(holder.getMetrics().getReceivedBytesCount(), lView.getBytesSent());
        Assert.assertEquals(MetricsService.sent.get(), sView.getBytesReceived());
        Assert.assertEquals(MetricsService.received.get(), sView.getBytesSent());

        httppost = new HttpPost("http://localhost:8280/service/metrics-echo-service?call=3");
        httppost.setEntity(new StringEntity(randomString(), "UTF-8"));
        response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        lView = transport.getTransportListener("http-8280");
        sView = transport.getTransportSender("http-sender");
        Assert.assertEquals(3, lView.getMessagesReceived());
        Assert.assertEquals(3, lView.getMessagesSent());
        Assert.assertEquals(holder.getMetrics().getSentBytesCount(), lView.getBytesReceived());
        Assert.assertEquals(holder.getMetrics().getReceivedBytesCount(), lView.getBytesSent());
        Assert.assertEquals(MetricsService.sent.get(), sView.getBytesReceived());
        Assert.assertEquals(MetricsService.received.get(), sView.getBytesSent());

        Assert.assertEquals(holder.getMetrics().getSentBytesCount() / 3, (long) lView.getAvgSizeReceived());
        Assert.assertEquals(holder.getMetrics().getReceivedBytesCount()/3 , (long) lView.getAvgSizeSent());
        Assert.assertEquals(MetricsService.sent.get() / 3, (long) sView.getAvgSizeReceived());
        Assert.assertEquals(MetricsService.received.get()/3 , (long) sView.getAvgSizeSent());
    }

    private String randomString() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<RANDOM.nextInt(10); i++) {
            sb.append(TEXT);
        }
        return sb.toString();
    }

    private static class Holder {
        private HttpConnectionMetrics metrics;

        public HttpConnectionMetrics getMetrics() {
            return metrics;
        }

        public void setMetrics(HttpConnectionMetrics metrics) {
            this.metrics = metrics;
        }
    }
}
