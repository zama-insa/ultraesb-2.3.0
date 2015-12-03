/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb;

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.api.BusRuntimeException;
import org.adroitlogic.ultraesb.api.Message;
import org.adroitlogic.ultraesb.api.format.MessageFormat;
import org.adroitlogic.ultraesb.api.management.AddressDefinition;
import org.adroitlogic.ultraesb.api.transport.BaseConstants;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.adroitlogic.ultraesb.core.ProxyService;
import org.adroitlogic.ultraesb.core.deployment.RootDeploymentUnit;
import org.adroitlogic.ultraesb.core.endpoint.Address;
import org.adroitlogic.ultraesb.core.endpoint.Endpoint;
import org.adroitlogic.ultraesb.core.format.ObjectMessage;
import org.adroitlogic.ultraesb.core.format.StringMessage;
import org.adroitlogic.ultraesb.jmx.JMXConstants;
import org.adroitlogic.ultraesb.jmx.core.*;
import org.adroitlogic.ultraesb.jmx.view.FileCacheView;
import org.adroitlogic.ultraesb.jmx.view.TransportView;
import org.adroitlogic.ultraesb.jmx.view.WorkManagerView;
import org.adroitlogic.ultraesb.transport.ResponseTrigger;
import org.adroitlogic.ultraesb.transport.TransportSender;
import org.adroitlogic.ultraesb.transport.vm.VMTransportSender;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.server.Server;
import samples.services.JettyUtils;
import samples.services.http.ErrorService;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.adroitlogic.ultraesb.jmx.JMXConstants.JMX_DOMAIN;
import static org.apache.http.Consts.UTF_8;

/**
 * Base class for all UltraESB n2n integration test cases. This base class is assuming JUnit4 and written to facilitate
 * a JUnit4 environment test framework.
 *
 * @author Ruwan
 * @since 2.3.0
 */
public abstract class AbstractUTestCase {

    /** Keeps track of the sample servers participating in the test */
    protected static Map<Integer, Server> sampleServers = new HashMap<Integer, Server>();
    /** An error HTTP service that is used for testing */
    protected static ErrorService errorService = new ErrorService();
    /** The default client to be used for HTTP invocations */
    protected final DefaultHttpClient client = new DefaultHttpClient();
    /** The platform MBean server of the system */
    protected static final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    /** The mbean names published by the ESB over there interfaces */
    protected static final Map<Class<? extends GenericMXBean>, String> mbeanNames
            = new HashMap<Class<? extends GenericMXBean>, String>();

    protected static final ContentType TEXT_XML = ContentType.create(BaseConstants.ContentType.TEXT_XML, UTF_8);
    protected static final ContentType APPLICATION_XML = ContentType.create(BaseConstants.ContentType.APPLICATION_XML, UTF_8);
    protected static final ContentType TEXT_PLAIN = ContentType.create(BaseConstants.ContentType.TEXT_PLAIN, UTF_8);
    protected static final ContentType APPLICATION_JSON = ContentType.create(BaseConstants.ContentType.APPLICATION_JSON, UTF_8);

    protected static final String SERVICE_URL_PREFIX = "http://localhost:8280/service/";
    protected static final String getQuoteRequest =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soap=\"http://soap.services.samples/\">\n" +
                    "   <soapenv:Body>\n" +
                    "      <soap:getQuote>\n" +
                    "         <request>\n" +
                    "            <symbol>ADRT</symbol>\n" +
                    "         </request>\n" +
                    "      </soap:getQuote>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

    protected Logger logger;
    protected static final Logger parentLogger = LoggerFactory.getLogger(AbstractUTestCase.class);
    protected int lastRespondedServer = 0;

    /** ESB starting time, based on the assumption of tests will be run one by one it is safe to keep this static */
    private static long startTime;

    static {
        mbeanNames.put(ServerManagementMXBean.class, JMXConstants.MXBEAN_NAME_SERVER_MANAGER);
        mbeanNames.put(ProxyServiceManagementMXBean.class, JMXConstants.MXBEAN_NAME_PROXY_SERVICES);
        mbeanNames.put(SequenceManagementMXBean.class, JMXConstants.MXBEAN_NAME_SEQUENCES);
        mbeanNames.put(EndpointManagementMXBean.class, JMXConstants.MXBEAN_NAME_ENDPOINTS);
        mbeanNames.put(TransportManagementMXBean.class, JMXConstants.MXBEAN_NAME_TRANSPORTS);
        mbeanNames.put(FileCacheManagementMXBean.class, JMXConstants.MXBEAN_NAME_FILE_CACHE);
        mbeanNames.put(WorkManagerManagementMXBean.class, JMXConstants.MXBEAN_NAME_WORK_MANAGERS);
    }

    protected AbstractUTestCase() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    /**
     * Starts the ESB with the given sample configuration, this method only accepts a single sample number or none
     * @param samples the sample number to be started
     */
    protected static void startESB(int... samples) {
        startTime = System.nanoTime();
        try {
            UltraServer.main(new String[]{"--confDir=conf", "--rootConf=" + (samples.length > 0 ?
                    "samples/conf/ultra-sample-" + samples[0] : "conf/ultra-root") + ".xml"});
        } catch (Throwable t) {
            parentLogger.error("Unexpected error starting the ESB", t);
            fail("Error in starting the UltraESB server " + (samples.length > 0 ? "with sample " + samples[0] : "")
                    + " due to : " + t.getMessage());
        }

        try {
            TransportManagementMXBean tmb = getMXBean(TransportManagementMXBean.class);
            // if http transports are defined, wait till they start
            int retries = 20;
            for (TransportView tv : tmb.getTransportListeners()) {
                if ("http".equals(tv.getType())) {
                    while (!"Started".equals(tv.getState()) && retries-- > 0) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignore) {}
                    }
                    if (retries > 0) {
                        parentLogger.info("Detected that the HTTP transports for the unit test has actually started");
                    }
                }
            }
        } catch (UndeclaredThrowableException ignore) {}
    }

    /**
     * Stops the started ESB
     */
    protected static void stopESB() {

        // first verify the smooth operation and if there is any resource leak
        try {
            Thread.sleep(1000);
            for (FileCacheView fcv : getMXBean(FileCacheManagementMXBean.class).getFileCaches()) {
                if (fcv.getAvailableForUse() != fcv.getTotalFilesCreated()) {
                    fail("File Cache : " + fcv.getId() + " available : " + fcv.getAvailableForUse() +
                            " total created : " + fcv.getTotalFilesCreated());
                }
            }

            for (WorkManagerView wmv : getMXBean(WorkManagerManagementMXBean.class).getWorkManagers()) {
                if (wmv.getWipMapSize() > 0) {
                    fail("Work Manager : " + wmv.getId() + " has pending messages");
                }
            }
        } catch (Throwable ignore) {}

        try {
            ServerManager.getInstance().shutdown();
        } catch (Throwable t) {
            parentLogger.error("Unexpected error stopping the ESB", t);
            fail("Error in stopping the UltraESB server due to : " + t.getMessage());
        }

        parentLogger.info("Test completed in {}", computeTimeTaken());
    }

    private static String computeTimeTaken() {
        long nanosTaken = System.nanoTime() - startTime;
        String result = (nanosTaken % 1000000) / 1000 + "Âµs";
        if (nanosTaken > 1000000) {
            long millisTaken = nanosTaken / 1000000;
            result = Long.toString(millisTaken % 1000) + "ms " + result;
            if (millisTaken > 1000) {
                long secondsTaken = millisTaken / 1000;
                result = Long.toString(secondsTaken % 60) + "sec " + result;
                if (secondsTaken > 60) {
                    result = Long.toString(secondsTaken / 60) + "min " + result;
                }
            }
        }
        return result;
    }

    /**
     * Start sample servers on the specified set of ports, if nothing specified it starts the sample server on the
     * default sample server port which is <code>9000</code>
     * @param ports the ports on which the sample servers need to be started
     */
    protected static void startSampleServers(int... ports) {
        if (ports.length > 0) {
            for (int port : ports) {
                startSampleServer(port);
            }
        } else {
            startSampleServer(9000);
        }
    }

    /**
     * Start a single sample server on the specified port
     * @param port the port on which the sample server be started
     */
    protected static void startSampleServer(int port) {
        try {
            Server server = new Server(port);
            server.setHandler(JettyUtils.sampleWebAppContext());
            server.start();
            sampleServers.put(port, server);
        } catch (Throwable t) {
            parentLogger.error("Unexpected error in starting the sample server", t);
            fail("Error in starting the sample server on port " + port + " due to : " + t.getMessage());
        }
    }

    /**
     * Stops all the started sample servers
     */
    protected static void stopSampleServers() {
        for (int serverPort : sampleServers.keySet()) {
            stopSampleServer(serverPort);
        }
    }

    /**
     * Stops a sample server previously started at the given port
     * @param port the port on which the started sample server to be stopped
     */
    protected static void stopSampleServer(int port) {
        try {
            sampleServers.get(port).stop();
        } catch (Throwable t) {
            parentLogger.error("Unexpected error stopping the sample server", t);
            fail("Error in stopping the sample server of port " + port + " due to : " + t.getMessage());
        }
    }

    /**
     * Starts an error service for testing
     */
    protected static void startErrorService() {
        errorService.start();
    }

    /**
     * Stops the started error service
     */
    protected static void stopErrorService() {
        errorService.stop();
    }

    /**
     * Gets the MXBean for the specified MXBean interface
     * @param mxBeanClass the interface that defines the MXBean to be retrieved
     * @param <T> the type of the MXBean interface
     * @return the MXBean instance or it will fail the test if the MXBean cannot be found
     */
    protected static <T extends GenericMXBean> T getMXBean(Class<T> mxBeanClass) {
        try {
            return JMX.newMXBeanProxy(mbs, new ObjectName(
                    JMX_DOMAIN + ":Name=" + mbeanNames.get(mxBeanClass)), mxBeanClass);
        } catch (MalformedObjectNameException e) {
            parentLogger.error("Unexpected error retrieving the MXBean", e);
            fail("The MXBean trying to retrieve " + mxBeanClass + "  is not retrievalbe due to : " + e.getMessage());
            return null;
        }
    }

    /**
     * Invokes the specified service URL with a default {@link #getQuoteRequest}, checks the response status code to be
     * <code>200 OK</code> and returns the response back, if anything fails the test will be failed by this method
     * @param serviceURL the complete service URL or the service name (will be prefixed by {@link #SERVICE_URL_PREFIX})
     * @return the string response if successful
     */
    protected String invokeWithSuccessCheck(String serviceURL) {
        return invokeWithSuccessCheck(serviceURL, getQuoteRequest);
    }

    /**
     * Invokes the specified service URL with the given request using text/xml content type, checks the response status
     * code to be <code>200 OK</code> and returns the response back, if anything fails the test will be failed
     * @param serviceURL the complete service URL or the service name (will be prefixed by {@link #SERVICE_URL_PREFIX})
     * @param requestContent the content to be sent to the given service URL
     * @return the string response if successful
     */
    protected String invokeWithSuccessCheck(String serviceURL, String requestContent) {
        return invokeWithSuccessCheck(serviceURL, new StringEntity(requestContent, TEXT_XML));
    }

    /**
     * Invokes the specified service URL with the given HTTP entity, checks the response status code to be
     * <code>200 OK</code> and returns the response back, if anything fails the test will be failed
     * @param serviceURL the complete service URL or the service name (will be prefixed by {@link #SERVICE_URL_PREFIX})
     * @param entity the content to be sent to the given service URL
     * @return the string response if successful
     */
    protected String invokeWithSuccessCheck(String serviceURL, HttpEntity entity) {
        HttpPost httppost = new HttpPost(serviceURL.contains("://") ? serviceURL : SERVICE_URL_PREFIX + serviceURL);
        httppost.setEntity(entity);
        return invokeWithSuccessCheck(httppost);
    }

    protected String invokeWithStatusCheck(String serviceURL, int status) {
        return invokeWithStatusCheck(serviceURL, new StringEntity(getQuoteRequest, TEXT_XML), status);
    }

    protected String invokeWithStatusCheck(String serviceURL, HttpEntity entity, int status) {
        HttpPost httppost = new HttpPost(serviceURL.contains("://") ? serviceURL : SERVICE_URL_PREFIX + serviceURL);
        httppost.setEntity(entity);
        return invokeWithStatusCheck(httppost, status);
    }

    protected synchronized String invokeWithSuccessCheck(HttpUriRequest request) {
        return invokeWithStatusCheck(request, HttpStatus.SC_OK);
    }

    protected synchronized String invokeWithStatusCheck(HttpUriRequest request, int status) {
        try {
            HttpResponse response = client.execute(request);
            assertEquals(status, response.getStatusLine().getStatusCode());
            String responseContent = EntityUtils.toString(response.getEntity());
            Header portHeader = response.getFirstHeader("port");
            if (portHeader != null) {
                lastRespondedServer = Integer.parseInt(portHeader.getValue());
            }
            logger.debug("Received the response : {}", responseContent);
            return responseContent;
        } catch (IOException e) {
            logger.error("Error in invoking the service {}", request.getURI(), e);
            fail("Error in invoking the service with the URL " + request.getURI() + " due to : " + e.getMessage());
            return null;
        }
    }

    protected String invokeGetWithSuccessCheck(String serviceURL) {
        return invokeWithSuccessCheck(
                new HttpGet(serviceURL.contains("://") ? serviceURL : SERVICE_URL_PREFIX + serviceURL));
    }

    /**
     * Invokes the specified {@code proxyId} via the specified VM transport sender via {@code vmSenderId} using the
     * specified {@code payload} payload.
     *
     * @param content the payload, properties and headers to use for the message submission via VM transport
     * @param proxyId the proxy service to which the message to be sent, it has to be in the configuration and should
     *                expose over the vm transport
     * @param timeout the timeout that the method waits for response after sending the message to the specified proxy
     * @return the payload received from the VM transport invocation over the specified proxy
     */
    @SuppressWarnings("deprecation")
    protected static MessageContent invokeViaVM(MessageContent content, String proxyId, Long timeout) {

        // prepare the mock proxy service and associated endpoints/address hierarchy
        RootDeploymentUnit rootDu = ServerManager.getInstance().getConfig().getRootDeploymentUnit();
        ProxyService mockPs = new ProxyService();
        mockPs.init(rootDu);
        mockPs.setWorkManager(ServerManager.getInstance().getConfig().getDefaultWorkManager());
        Endpoint mockEp = new Endpoint();
        Address mockAddress = new Address();
        mockAddress.setAddressType(AddressDefinition.Type.RESPONSE);
        mockEp.setAddressList(Arrays.asList(mockAddress));
        mockEp.init(rootDu);
        mockEp.start();
        mockPs.setOutDestination(mockEp);

        // construct the message to be injected
        MessageImpl msg = new MessageImpl(true, mockPs, "test");
        msg.setCurrentPayload(content.payload);
        for (Map.Entry<String, Object> property : content.properties.entrySet()) {
            msg.addMessageProperty(property.getKey(), property.getValue());
        }
        for (Map.Entry<String, List<String>> header : content.headers.entrySet()) {
            for (String headerVal : header.getValue()) {
                msg.addTransportHeader(header.getKey(), headerVal);
            }
        }

        // prepare and attach a test response trigger to make sure we get the response after the vm invocation as the
        // VM transport send is asynchronous
        TestResponseTrigger trigger = new TestResponseTrigger();
        msg.setResponseTrigger(trigger);
        msg.setDestinationURL("vm://" + proxyId);

        // find a VM transport sender and use it to send the prepared message to the intended proxy service
        boolean sent = false;
        for (TransportSender sender : rootDu.getTransportSenders().values()) {
            if (sender instanceof VMTransportSender) {
                sender.send(msg);
                sent = true;
                break;
            }
        }

        if (sent) { // if successfully sent wait for the response
            long timeoutAt = System.currentTimeMillis() + timeout;
            while (!trigger.content.isComplete()) {
                try {
                    Thread.sleep(100);
                    if (timeout > 0 && System.currentTimeMillis() >= timeoutAt) { // if the specified timeout has passed
                        // trigger a runtime exception for the timeout and stop waiting for the response
                        throw new BusRuntimeException("Timeout waiting for the response");
                    }
                } catch (InterruptedException ignore) {}
            }
            return trigger.content;
        } else {
            throw new BusRuntimeException("Message could not be sent");
        }
    }

    /**
     * Invokes the given proxy service over the VM transport with the given typed object as the request payload via an
     * ObjectMessage, and expects the response to contain an ObjectMessage with the same type of an object as the payload
     *
     * @param payload the payload object
     * @param proxyId the proxy service to which the message to be sent, it has to be in the configuration and should
     *                expose over the vm transport
     * @param timeout the timeout that the method waits for response after sending the message to the specified proxy
     * @param <T> the type of the payload object and the response object returned by the service invocation
     * @return the resulting object of the given type returned from the invocation response
     */
    protected static <T> T invokeTypedObjectViaVM(T payload, String proxyId, Long timeout) {
        MessageContent responseContent = invokeViaVM(MessageContent.forObject(payload), proxyId, timeout);
        if (responseContent.payload instanceof ObjectMessage) {
            ObjectMessage responseMsg = (ObjectMessage) responseContent.payload;
            return (T) responseMsg.getObject();
        } else {
            throw new BusRuntimeException("The response received is not an Object Message");
        }
    }

    /**
     * Copies the source directory into the destination directory and ensures to delete the destination
     * on unit test exit
     * @param source the source directory to be copied
     * @param destination the destination to which the above source directory will be copied
     */
    protected void copyDirectoryForTest(File source, AutoDeletableDirectory destination) {
        try {
            FileUtils.copyDirectory(source, destination);
            FileUtils.forceDeleteOnExit(destination);
        } catch (IOException e) {
            logger.error("Unexpected error copying directories from {} to {}", source, destination, e);
            fail("Copying directories from " + source + " to " + destination + " failed due to : " + e.getMessage());
        }
    }

    /**
     * Makes sure the directory is deleted after use if it is initiated in a try block
     */
    protected class AutoDeletableDirectory extends File implements Closeable {

        public AutoDeletableDirectory(String directory) {
            super(directory);
        }

        @Override
        public void close() throws IOException {
            FileUtils.deleteDirectory(this);
        }
    }

    /**
     * A response trigger that helps the response to be collected for any direct VM transport invocations
     */
    protected static class TestResponseTrigger implements ResponseTrigger {

        volatile MessageContent content = new MessageContent();

        @Override
        public void submitResponse(MessageImpl message, Integer statusCode) {
            content.fillResponse(message);
        }
    }

    /**
     * The message content that helps specifying message payload, properties and headers for the VM invokes
     */
    protected static class MessageContent {

        public MessageFormat payload = new ObjectMessage(new Object());
        public Map<String, Object> properties = new HashMap<String, Object>();
        public Map<String, List<String>> headers = new HashMap<String, List<String>>();
        volatile boolean complete;

        public MessageContent addProperty(String key, Object value) {
            properties.put(key, value);
            return this;
        }

        public <T> T getProperty(String key, Class<T> type) {
            return type.cast(properties.get(key));
        }

        public MessageContent addHeader(String name, String value) {
            List<String> headerValues = headers.get(name);
            if (headerValues == null) {
                headerValues = new ArrayList<String>();
                headers.put(name, headerValues);
            }
            headerValues.add(value);
            return this;
        }

        public String getHeader(String name) {
            return headers.containsKey(name) && !headers.get(name).isEmpty() ? headers.get(name).get(0) : null;
        }

        public List<String> getAllHeaderValues(String name) {
            return headers.containsKey(name) ? headers.get(name) : Collections.<String>emptyList();
        }

        void fillResponse(Message msg) {
            payload = msg.getCurrentPayload();
            properties = msg.getMessageProperties();
            headers = msg.getDuplicateTransportHeaders();
            complete = true;
        }

        public boolean isComplete() {
            return complete;
        }

        public static MessageContent forObject(Object object) {
            MessageContent content = new MessageContent();
            content.payload = new ObjectMessage(object);
            return content;
        }

        public static MessageContent forString(String payload) {
            MessageContent content = new MessageContent();
            content.payload = new StringMessage(payload);
            return content;
        }

        @Override
        public String toString() {
            return payload.toString();
        }
    }
}
