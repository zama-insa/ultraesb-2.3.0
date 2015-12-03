/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.LoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;

/**
 * @author Ruwan
 */
public class QueryParamTest extends UTestCase {

    private static final Logger logger = LoggerFactory.getLogger(QueryParamTest.class);

    private static HttpClient client = new DefaultHttpClient();

    public static Test suite() {
        return new TestSetup(new TestSuite(QueryParamTest.class)) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[] {"--confDir=conf", "--rootConf=../modules/sample/src/test/conf/query-param-test.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
            }
        };
    }

    public void testQueryParamsViaProperty() throws Exception {
        verifyOutput("echo-back-direct?foo=bar", "foo=bar");
        verifyOutput("echo-back-modify-map?foo=bar", "foo=barmodified");
        verifyOutput("echo-back-modify-map-wdups?foo=bar", "foo=barmodifiedwdups");
    }

    public void testQueryParamViaAPI() throws Exception {
        verifyOutput("echo-back-modify-api?foo=bar", "foo=barmodifiedapi");
        verifyOutput("echo-back-add-new-api?foo=bar", "foo=bar&foo2=barmodifiedapi", "foo2=barmodifiedapi&foo=bar");
        verifyOutput("echo-back-add-dup-api?foo=bar", "foo=bar&foo=barmodifiedapi", "foo=barmodifiedapi&foo=bar");
        verifyOutput("echo-back-add-dup-api?foo=bar&x=y", "x=y");
    }

    private void verifyOutput(String serviceString, String... responseStrings) throws Exception {

        HttpGet httpget = new HttpGet("http://localhost:8280/service/" + serviceString);
        HttpResponse response = client.execute(httpget);
        String responseContent = EntityUtils.toString(response.getEntity());
        logger.info("Response : {}", responseContent);
        for (String responseString : responseStrings) {
            if (responseContent.contains(responseString)) {
                return;
            }
        }
        Assert.fail("Non of the responses passed " + Arrays.asList(responseStrings).toString() + " matched");
    }
}

