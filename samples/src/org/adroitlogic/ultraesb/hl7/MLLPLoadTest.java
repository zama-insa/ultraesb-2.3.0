/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.hl7;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.RandomTimer;
import com.clarkware.junitperf.Timer;
import junit.extensions.TestSetup;
import junit.framework.Test;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;

/**
 * @author asankha
 */
public class MLLPLoadTest extends UTestCase {

    public static Test suite() throws Exception {
        Test testCase = new HL7LoadTestCase("testAckOverMLLP");
        Timer timer = new RandomTimer(1000, 500);
        Test loadTest = new LoadTest(testCase, 10, 5, timer);

        return new TestSetup(loadTest) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-751.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
            }
        };
    }
}

