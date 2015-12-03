/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.adroitlogic.ultraesb.Util;
import org.adroitlogic.ultraesb.core.deployment.DeploymentUnit;
import org.adroitlogic.ultraesb.jmx.JMXConstants;
import org.adroitlogic.ultraesb.jmx.core.EndpointManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.ProxyServiceManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.SequenceManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.ServerManagementMXBean;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.util.EntityUtils;

import javax.management.JMX;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.Future;

/**
 * @author asankha
 */
public class ConfigurationManagementTest extends UTestCase {

    private static final String DU_DEFAULT = "default";
    private static final String SEQ_ERROR_HANDLER = "error-handler";
    private static final String EP_ECHO_SERVICE = "echo-service";
    private static final String SEQ_ECHO_PROXY_OUT_SEQUENCE = "echo-proxy-outSequence";
    private static final String SEQ_ECHO_BACK_IN_SEQUENCE = "echo-back-inSequence";

    public static Test suite() {
        return new UTestSetup(ConfigurationManagementTest.class);
    }

    public void testChildConfigurationSupport() throws Exception {

        new File("conf/test-update.xml").deleteOnExit();

        ServerManagementMXBean smb = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_SERVER_MANAGER), ServerManagementMXBean.class);

        ProxyServiceManagementMXBean psm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_PROXY_SERVICES), ProxyServiceManagementMXBean.class);

        SequenceManagementMXBean seq = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_SEQUENCES), SequenceManagementMXBean.class);

        EndpointManagementMXBean epm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_ENDPOINTS), EndpointManagementMXBean.class);


        // check if default config is loaded
        Assert.assertNotNull(seq.getSequence(SEQ_ERROR_HANDLER));
        Assert.assertNotNull(epm.getEndpoint(EP_ECHO_SERVICE));
        Assert.assertNotNull(seq.getSequence(SEQ_ECHO_PROXY_OUT_SEQUENCE)); //
        Assert.assertNotNull(seq.getSequence(SEQ_ECHO_BACK_IN_SEQUENCE));
        Assert.assertTrue(smb.getServerManagerView().getDeploymentUnits().containsKey(DU_DEFAULT));


        // unload ultra-dynamic.xml and check its effectiveness, the parts from ultra-custom.xml would still be there
        smb.unloadDeploymentUnit(DU_DEFAULT);
        Assert.assertNotNull(seq.getSequence(SEQ_ERROR_HANDLER));
        Assert.assertNotNull(epm.getEndpoint(EP_ECHO_SERVICE));
        try {
            Assert.assertNull(seq.getSequence(SEQ_ECHO_PROXY_OUT_SEQUENCE));
        } catch (IllegalArgumentException ignore) {}
        try {
            Assert.assertNull(seq.getSequence(SEQ_ECHO_BACK_IN_SEQUENCE));
        } catch (IllegalArgumentException ignore) {}
        Assert.assertEquals(smb.getServerManagerView().getDeploymentUnits().get(DU_DEFAULT).getState(),
                DeploymentUnit.State.CREATED.toString());

        // reload ultra-dynamic.xml
        smb.addOrUpdateDeploymentUnit(DU_DEFAULT);
        Assert.assertNotNull(seq.getSequence(SEQ_ECHO_PROXY_OUT_SEQUENCE));
        Assert.assertNotNull(seq.getSequence(SEQ_ECHO_BACK_IN_SEQUENCE));
        Assert.assertNotNull(epm.getEndpoint(EP_ECHO_SERVICE));
        Assert.assertTrue(smb.getServerManagerView().getDeploymentUnits().containsKey(DU_DEFAULT));

        // load additional child config from config-x as test-update.xml
        Util.copyStreams(
            new FileInputStream("samples/resources/sample-child-config-x.xml"),
            new FileOutputStream("conf/test-update.xml"));
        smb.addOrUpdateConfigFromFile("test-update.xml");

        // check that the config-x stuff is now loaded and working
        Assert.assertNotNull(psm.getProxyService("echo-proxy-dup"));
        Assert.assertNotNull(psm.getProxyService("outdated-proxy"));

        // and returning us "X" for echo-back-dup
        HttpClient client = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://localhost:8280/service/echo-back-dup");
        httppost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        httppost.addHeader("SOAPAction", "\"urn:getQuote\"");
        HttpResponse response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("X", EntityUtils.toString(response.getEntity()));

        // outdated proxy of config-x should be available now before we switch
        HttpPost outPost = new HttpPost("http://localhost:8280/service/outdated-proxy");
        outPost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        outPost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(outPost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Z", EntityUtils.toString(response.getEntity()));

        // load config-y as test-update.xml now, outdating the config-x stuff
        Util.copyStreams(
            new FileInputStream("samples/resources/sample-child-config-y.xml"),
            new FileOutputStream("conf/test-update.xml"));
        smb.addOrUpdateConfigFromFile("test-update.xml");

        // outdated proxy of config-x must not be available anymore as its not defined in config-y
        try {
            psm.getProxyService("outdated-proxy");
            fail("outdated proxy still available via JMX");
        } catch (Exception e) {}
        outPost = new HttpPost("http://localhost:8280/service/outdated-proxy");
        outPost.setEntity(new StringEntity(getQuoteRequest, TEXT_XML));
        outPost.addHeader("SOAPAction", "\"urn:getQuote\"");
        response = client.execute(outPost);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatusLine().getStatusCode());
        EntityUtils.consume(response.getEntity());

        // echo-back-dup proxy should now be updated and return us "Y"
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Y", EntityUtils.toString(response.getEntity()));

        // purge older version of test-update.xml
        smb.purgeAllOutdatedDeploymentUnits();

        // that should not affect the newly loaded versions
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Y", EntityUtils.toString(response.getEntity()));

        // now unload config-y and re-load config-x as test-update
        Util.copyStreams(
            new FileInputStream("samples/resources/sample-child-config-x.xml"),
            new FileOutputStream("conf/test-update.xml"));
        smb.addOrUpdateConfigFromFile("test-update.xml");

        // ensure config-x works as expected again returning us "X"
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("X", EntityUtils.toString(response.getEntity()));

        // now send a request to config-x service and quickly switch the config to config-y - We will use the Aync client
        HttpAsyncClient httpclient = new DefaultHttpAsyncClient();
        httpclient.start();
        try {
            HttpPost asyncPost = new HttpPost("http://localhost:8280/service/echo-proxy-dup");
            asyncPost.setEntity(new StringEntity("X", TEXT_PLAIN));
            asyncPost.addHeader("SOAPAction", "\"urn:getQuote\"");
            asyncPost.addHeader("sleep", "8000");
            Future<HttpResponse> future = httpclient.execute(asyncPost, null);

            logger.info("Sent a request to OLD service");

            // load config-y as test-update
            Util.copyStreams(
                new FileInputStream("samples/resources/sample-child-config-y.xml"),
                new FileOutputStream("conf/test-update.xml"));
            smb.addOrUpdateConfigFromFile("test-update.xml");

            // even though the config switched, we should still get the reply via the config-x version of the proxy
            logger.info("Switched service and waiting for response");
            response = future.get();
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            Assert.assertEquals("OLD:X", EntityUtils.toString(response.getEntity()));
            logger.info("Received response and was processed by OLD service..");

        } finally {
            httpclient.shutdown();
            new File("conf/test-update.xml").delete();
        }

        // but any new request should use config-y now
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Y", EntityUtils.toString(response.getEntity()));

        // unload test-update (config-x which is outdated)
        smb.purgeAllOutdatedDeploymentUnits();

        //still should use config-y
        response = client.execute(httppost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertEquals("Y", EntityUtils.toString(response.getEntity()));

        // merging child config with non-root reference sequence should fail
        try {
            Util.copyStreams(
                    new FileInputStream("samples/resources/invalid-child-config-b.xml"),
                    new FileOutputStream("conf/invalid-child-config-b.xml"));
            smb.addOrUpdateConfigFromFile("invalid-child-config-b.xml");
            fail("child config with non-root reference should not be loaded");
        } catch (Exception e) {
        } finally {
            new File("conf/invalid-child-config-b.xml").deleteOnExit();
            new File("conf/invalid-child-config-b.xml").delete();
        }
    }
}
