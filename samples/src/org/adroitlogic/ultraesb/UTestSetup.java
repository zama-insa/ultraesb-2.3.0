/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import samples.services.JettyUtils;

/**
 * Generic test suite setup that will be used by the UTestCase classes to setup the sample services and the ESB with
 * the sample configuration
 *
 * @author Ruwan
 * @since 2.1.0
 */
public class UTestSetup extends TestSetup {

    protected static Logger logger;
    protected Server server;
    private int sampleNumber;
    private int serverPort;
    private long startTime;

    public UTestSetup(Class<? extends TestCase> test) {
        super(new TestSuite(test));
        logger = LoggerFactory.getLogger(test);
    }

    public UTestSetup(Class<? extends TestCase> test, int sampleNumber) {
        this(test);
        this.sampleNumber = sampleNumber;
    }

    public UTestSetup(Test test, int sampleNumber, Class<? extends TestCase> testClass) {
        super(test);
        this.sampleNumber = sampleNumber;
        logger = LoggerFactory.getLogger(testClass);
    }

    public UTestSetup(Class<? extends TestCase> test, int sampleNumber, int serverPort) {
        this(test, sampleNumber);
        this.serverPort = serverPort;
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        startTime = System.nanoTime();
        UltraServer.main(new String[]{"--confDir=conf", "--rootConf=" + (sampleNumber > 0 ?
                "samples/conf/ultra-sample-" + sampleNumber : "conf/ultra-root") + ".xml"});
        server = new Server(serverPort > 0 ? serverPort : 9000);
        server.setHandler(JettyUtils.sampleWebAppContext());
        server.start();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        ServerManager.getInstance().shutdown(3000);
        if (server != null) {
            server.stop();
        }
        long milliDiff = (System.nanoTime() - startTime)/1000000;
        logger.info("Completed in {} seconds and {} milliseconds", milliDiff / 1000, milliDiff % 1000);
    }

    public void startServer() throws Exception {
        server.start();
    }

    public void stopServer() throws Exception {
        server.stop();
    }
}
