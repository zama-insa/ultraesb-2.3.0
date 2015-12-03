/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.endpoints;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.ultraesb.api.transport.http.HttpConstants;
import org.adroitlogic.ultraesb.jmx.JMXConstants;
import org.adroitlogic.ultraesb.jmx.core.EndpointManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.FileCacheManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.TransportManagementMXBean;
import org.adroitlogic.ultraesb.jmx.view.EndpointView;
import org.adroitlogic.ultraesb.jmx.view.TransportView;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import samples.services.JettyUtils;
import samples.services.http.ErrorService;

import javax.management.JMX;
import javax.management.ObjectName;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * @author asankha
 */
public class EndpointStatisticsTest extends UTestCase {

    private static ErrorService errorService = new ErrorService();
    private static Server server = new Server(9001);

    private static HttpClient client = new DefaultHttpClient();

    private static void resetClient() {
        client.getConnectionManager().shutdown();
        client = new DefaultHttpClient();
    }

    public static Test suite() {

        return new TestSetup(new TestSuite(EndpointStatisticsTest.class)) {

            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-603.xml"});
                errorService.start();
                server.setHandler(JettyUtils.sampleWebAppContext());
                server.start();
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                server.stop();
                errorService.stop();
            }
        };
    }

    public void testRun() throws Exception {
        basic();
        timeouts();
        transportSenderFaultAndSuccess();
        clientTimeouts();
    }

    public void basic() throws Exception {

        EndpointManagementMXBean epm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_ENDPOINTS), EndpointManagementMXBean.class);

        epm.resetStatistics("ep-proxy-inDestination");

        EndpointView epv;
        HttpPost httppost;
        HttpResponse response;

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(0, epv.getProcessedMessageCount());

        // make a normal call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=1");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(1, epv.getProcessedMessageCount());
        Assert.assertEquals(0, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        // stop service and let address suspend  ----------------------------------------
        errorService.stop();

        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=2");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(2, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        // start service and let address recover----------------------------------------
        errorService.start();
        // give a second for address to become ready for retry
        Thread.sleep(1200);

        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=1");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(3, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        // make a call again, but make service close socket while reading ------------
        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=3");
        httppost.addHeader("close-read", "true");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(4, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());
        Assert.assertEquals(0, epv.getWarnAddressCount());

        //retry again within the grace period
        Thread.sleep(50);
        // close socket while service writes response

        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=4");
        httppost.addHeader("close-write", "true");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(5, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorReceivingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());
        Assert.assertEquals(0, epv.getWarnAddressCount());

        // still not suspended, now sleep a bit to make the endpoint suspend on next error
        Thread.sleep(1000);

        // another failing send again, this should cause the address to be suspended
        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=5");
        httppost.addHeader("close-write", "true");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(6, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());
        Assert.assertEquals(2, epv.getTemporaryErrorReceivingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        //let address recover from temporary error ----------------------------------
        Thread.sleep(1200);
    }


    public void timeouts() throws Exception {

        EndpointManagementMXBean epm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_ENDPOINTS), EndpointManagementMXBean.class);

        FileCacheManagementMXBean fcm = JMX.newMXBeanProxy(mbs, new ObjectName(
        JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_FILE_CACHE), FileCacheManagementMXBean.class);

        epm.resetStatistics("ep-proxy-inDestination");

        EndpointView epv;
        HttpPost httppost;
        HttpResponse response;

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(0, epv.getProcessedMessageCount());

        // make a normal call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=1");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(1, epv.getProcessedMessageCount());
        Assert.assertEquals(0, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        // make a endpoint timeout call while sending ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=2");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        httppost.addHeader("sleep-read", "3000");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(2, epv.getProcessedMessageCount());
        Assert.assertEquals(0, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        // let connection timeout
        Thread.sleep(1000);

        // make a endpoint timeout (and suspend) while receiving ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/ep-proxy?call=3");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        httppost.addHeader("sleep-write", "3000");
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("ep-proxy-inDestination");
        Assert.assertEquals(3, epv.getProcessedMessageCount());
        Assert.assertEquals(0, epv.getSuspendErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorReceivingMessageCount());
        Assert.assertEquals(0, epv.getSuspendedAddressCount());
        Assert.assertEquals(1, epv.getActiveAddressCount());

        Thread.sleep(2000);
        Assert.assertEquals(10, fcm.getFileCache("fileCache").getAvailableForUse());

    }

    public void transportSenderFaultAndSuccess() throws Exception {

        resetClient();

        EndpointManagementMXBean epm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_ENDPOINTS), EndpointManagementMXBean.class);

        epm.resetStatistics("invalid-ep-proxy-inDestination");

        EndpointView epv;
        HttpPost httppost;
        HttpResponse response;

        epv = epm.getEndpoint("invalid-ep-proxy-inDestination");
        Assert.assertEquals(0, epv.getProcessedMessageCount());

        // make a normal call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/invalid-ep-proxy?call=1");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        epv = epm.getEndpoint("invalid-ep-proxy-inDestination");
        Assert.assertEquals(1, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuspendErrorSendingMessageCount());
    }

    public void clientTimeouts() throws Exception {

        EndpointManagementMXBean epm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_ENDPOINTS), EndpointManagementMXBean.class);

        Thread.sleep(5000);
        epm.resetStatistics("mediation.response");

        EndpointView epv;
        HttpPost httppost;
        HttpResponse response;

        epv = epm.getEndpoint("mediation.response");
        Assert.assertEquals(0, epv.getProcessedMessageCount());
        Assert.assertEquals(0, epv.getSuccessfulMessageCount());

        // set default connection timeout to 2S
        client.getParams().setIntParameter("http.socket.timeout", 2000);

        // make a normal call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/client-timeout-3");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        Thread.sleep(1000);
        epv = epm.getEndpoint("mediation.response");
        Assert.assertEquals(1, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuccessfulMessageCount());

        // make a timeout call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/client-timeout-3");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        httppost.addHeader("sleep", "true");
        try {
            client.execute(httppost);
            fail("socket timeout did not occur");
        } catch (SocketTimeoutException e) {}

        Thread.sleep(3000); // as UE detects the socket timeout later
        epv = epm.getEndpoint("mediation.response");
        Assert.assertEquals(2, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuccessfulMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());

        // make a timeout call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/client-timeout-1");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        try {
            client.execute(httppost);
            fail("socket timeout did not occur");
        } catch (SocketTimeoutException e) {}

        Thread.sleep(4000); // as UE detects the socket timeout later
        epv = epm.getEndpoint("client-timeout-1-outDestination");
        Assert.assertEquals(1, epv.getProcessedMessageCount());
        Assert.assertEquals(0, epv.getSuccessfulMessageCount());
        Assert.assertEquals(1, epv.getTemporaryErrorSendingMessageCount());

        // make a timeout call ----------------------------------------------------------
        httppost = new HttpPost("http://localhost:8280/service/client-timeout-2");
        httppost.setEntity(new StringEntity("Hello World", TEXT_PLAIN));
        try {
            client.execute(httppost);
            fail("socket timeout did not occur");
        } catch (SocketTimeoutException e) {}

        Thread.sleep(4000); // as UE detects the socket timeout later
        epv = epm.getEndpoint("mediation.response");
        Assert.assertEquals(3, epv.getProcessedMessageCount());
        Assert.assertEquals(1, epv.getSuccessfulMessageCount());
        Assert.assertEquals(2, epv.getTemporaryErrorSendingMessageCount());
        Assert.assertEquals(0, epv.getTemporaryErrorReceivingMessageCount());

        // make the ESB listener timeout ----------------------------------------------------------
        Socket socket = null;
        try {
            socket            = new Socket("localhost", 8280);
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            wr.write("POST /service/client-timeout-4 HTTP/1.1\r\n" +
                "Host: localhost:8280\r\n" +
                "Content-Type: text/xml\r\n" +
                "Content-Length: 13\r\n\r\n" +
                "Hello Asa");
            wr.flush();

            Thread.sleep(12000); // as UE detects the socket timeout later

        } catch (Exception e) {
            fail("Raw socket client failed" + e.getMessage());
        } finally {
            if (socket != null) {
                socket.close();
            }
        }

        TransportManagementMXBean transport = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_TRANSPORTS), TransportManagementMXBean.class);
        TransportView lView = transport.getTransportListener("http-8280");
        Assert.assertEquals(Long.valueOf(1), lView.getReceiveErrorCodeTable().get(HttpConstants.ErrorCodes.LST_TIMEOUT));
    }
}
