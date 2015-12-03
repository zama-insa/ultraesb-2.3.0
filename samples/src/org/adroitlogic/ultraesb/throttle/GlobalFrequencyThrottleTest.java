/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.throttle;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * @author Ruwan
 * @since 1.7.0
 */
public class GlobalFrequencyThrottleTest extends UTestCase {

    static {
        System.setProperty("ehcache.disk.store.dir", System.getProperty("java.io.tmpdir"));
    }

    public static Test suite() {
        return new UTestSetup(GlobalFrequencyThrottleTest.class, 801);
    }

    public void testThrottle() throws Exception {

        for (int i = 1; i <=3; i++) {
            HttpGet httpget = new HttpGet("http://localhost:8280/service/counter");
            HttpResponse response = client.execute(httpget);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
            Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Success : count " + i));
        }

        HttpGet httpget = new HttpGet("http://localhost:8280/service/counter");
        HttpResponse response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatusLine().getStatusCode());
        Assert.assertTrue(EntityUtils.toString(response.getEntity()).contains("Failure : count 4"));
    }
}
