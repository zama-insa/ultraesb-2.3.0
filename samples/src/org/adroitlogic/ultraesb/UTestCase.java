/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb;

import junit.framework.TestCase;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.api.transport.BaseConstants;
import org.adroitlogic.ultraesb.jmx.JMXConstants;
import org.adroitlogic.ultraesb.jmx.core.FileCacheManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.TransportManagementMXBean;
import org.adroitlogic.ultraesb.jmx.core.WorkManagerManagementMXBean;
import org.adroitlogic.ultraesb.jmx.view.FileCacheView;
import org.adroitlogic.ultraesb.jmx.view.TransportView;
import org.adroitlogic.ultraesb.jmx.view.WorkManagerView;
import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author asankha
 * @author Ruwan
 *
 * A simple JUnit TestCase extension to detect possible file leaks from the file-caches. End users creating
 * UltraESB unit test cases may extend from this class, as it will be beneficiary in checking memory leaks and also
 * ease of testcase writing
 */
public class UTestCase extends TestCase {

    protected static Logger logger;

    protected static final ContentType TEXT_XML
            = ContentType.create(BaseConstants.ContentType.TEXT_XML, Consts.UTF_8);
    protected static final ContentType APPLICATION_XML
            = ContentType.create(BaseConstants.ContentType.APPLICATION_XML, Consts.UTF_8);
    protected static final ContentType TEXT_PLAIN
            = ContentType.create(BaseConstants.ContentType.TEXT_PLAIN, Consts.UTF_8);
    protected static final ContentType APPLICATION_JSON
            = ContentType.create(BaseConstants.ContentType.APPLICATION_JSON, Consts.UTF_8);

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

    protected static final DefaultHttpClient client = new DefaultHttpClient();
    protected final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

    protected UTestCase() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    protected UTestCase(String name) {
        super(name);
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    protected void tearDown() throws Exception {

        Thread.sleep(1000);

        FileCacheManagementMXBean fcm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_FILE_CACHE), FileCacheManagementMXBean.class);

        for (FileCacheView fcv : fcm.getFileCaches()) {
            if (fcv.getAvailableForUse() != fcv.getTotalFilesCreated()) {
                fail("File Cache : " + fcv.getId() + " available : " + fcv.getAvailableForUse() +
                    " total created : " + fcv.getTotalFilesCreated());
            }
        }

        WorkManagerManagementMXBean wmm = JMX.newMXBeanProxy(mbs, new ObjectName(
            JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_WORK_MANAGERS), WorkManagerManagementMXBean.class);

        for (WorkManagerView wmv : wmm.getWorkManagers()) {
            if (wmv.getWipMapSize() > 0) {
                fail("Work Manager : " + wmv.getId() + " has pending messages");
            }
        }
        super.tearDown();
    }

    @Override
    protected void setUp() throws Exception {

        try {
            TransportManagementMXBean tmb = JMX.newMXBeanProxy(mbs, new ObjectName(
                JMXConstants.JMX_DOMAIN + ":Name=" + JMXConstants.MXBEAN_NAME_TRANSPORTS), TransportManagementMXBean.class);

            // if http transports are defined, wait till they start
            int retries = 20;
            for (TransportView tv : tmb.getTransportListeners()) {
                if("http".equals(tv.getType())) {
                    while (!"Started".equals(tv.getState()) && retries-- > 0) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignore) {}
                    }
                    if (retries > 0) {
                        logger.info("Detected that the HTTP transports for the unit test has actually started");
                    }
                }
            }
            super.setUp();

        } catch (UndeclaredThrowableException ignore) {}
    }
}
