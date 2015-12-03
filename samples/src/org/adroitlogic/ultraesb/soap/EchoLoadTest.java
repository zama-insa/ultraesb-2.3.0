/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.RandomTimer;
import com.clarkware.junitperf.Timer;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;

/**
 * @author asankha
 */
public class EchoLoadTest extends UTestCase {

    public static Test suite() throws Exception {

        Timer timer = new RandomTimer(100, 50);                 /* (delay, variation) */
        Test echoTestCase = new MultiplicationLoadTestCase("testEcho");
        Test echoLoadTest = new LoadTest(echoTestCase, 20, 500, timer);    /* (testcase, concurrency, count, timer) */
        return new UTestSetup(echoLoadTest, 208, EchoLoadTest.class);
    }
}
