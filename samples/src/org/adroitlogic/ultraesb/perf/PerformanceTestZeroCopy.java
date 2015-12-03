/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.perf;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.RandomTimer;
import com.clarkware.junitperf.Timer;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.LoggerFactory;
import org.eclipse.jetty.server.Server;
import samples.services.JettyUtils;

/**
 * @author asankha
 */
public class PerformanceTestZeroCopy extends TestCase {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestZeroCopy.class);

    protected static String configFile = "samples/conf/ultra-sample-950.xml";

    private static Server server = null;

    public static Test suite() throws Exception {

        Timer timer = new RandomTimer(2, 1);                               /* (delay, variation) */
        TestSuite suite = new TestSuite();                                 /* (testcase, concurrency, count, timer) */

        final PerformanceTestCase cbrTestCase = new PerformanceTestCase("testCBR");
        final PerformanceTestCase directTestCase = new PerformanceTestCase("testDirect");
        final PerformanceTestCase xsltTestCase = new PerformanceTestCase("testXSLT");
        final PerformanceTestCase secureTestCase = new PerformanceTestCase("testSecure");

        suite.addTest(new LoadTest(directTestCase, 20, 5, timer));
        suite.addTest(new LoadTest(cbrTestCase, 20, 100, timer));
        suite.addTest(new LoadTest(xsltTestCase, 20, 100, timer));
        suite.addTest(new LoadTest(secureTestCase, 20, 100, timer));

        return new TestSetup(suite) {

            private long startTime;

            protected void setUp() throws Exception {
                server = new Server(9000);
                server.setHandler(JettyUtils.sampleWebAppContext());
                server.start();

                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=" + configFile});
                startTime = System.currentTimeMillis();
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                System.out.println("Completed in : " + (System.currentTimeMillis() - startTime)/1000 + " seconds");
                if (server != null) {
                    server.stop();
                }
                logger.info("Direct Time : {}ms" + directTestCase.getNanoDirect() / 1000000);
                logger.info("CBR Time : {}ms" + cbrTestCase.getNanoCBR() / 1000000);
                logger.info("XSLT Time : {}ms" + xsltTestCase.getNanoXSLT() / 1000000);
                logger.info("Secure Time : {}ms" + secureTestCase.getNanoSec() / 1000000);
            }
        };
    }
}
