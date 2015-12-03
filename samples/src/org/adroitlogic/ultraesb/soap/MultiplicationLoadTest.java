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
public class MultiplicationLoadTest extends UTestCase {

    public static Test suite() throws Exception {

        Timer timer = new RandomTimer(100, 50);                 /* (delay, variation) */
        Test multiTestCase = new MultiplicationLoadTestCase("testMultiplication");
        Test multiLoadTest = new LoadTest(multiTestCase, 10, 100, timer);    /* (testcase, concurrency, count, timer) */
        return new UTestSetup(multiLoadTest, 208, MultiplicationLoadTest.class);
    }
}
