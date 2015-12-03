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
public class XSLTLoadTest extends UTestCase {

    public static Test suite() throws Exception {

        Test testCase = new XSLTLoadTestCase("testSimpleXSLT");
        Timer timer = new RandomTimer(100, 50);                 /* (delay, variation) */
        Test loadTest = new LoadTest(testCase, 20, 50, timer);    /* (testcase, concurrency, count, timer) */
        return new UTestSetup(loadTest, 205, XSLTLoadTest.class);
    }
}
