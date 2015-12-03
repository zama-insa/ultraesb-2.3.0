/*
 * AdroitLogic UltraESB Enterprise Service Bus
 *
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 *
 * GNU Affero General Public License Usage
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
 * If not, see http://www.gnu.org/licenses/agpl-3.0.html
 *
 * Commercial Usage
 *
 * Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
 * License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
 * agreement between you and AdroitLogic.
 *
 * If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
 * please contact AdroitLogic at info@adroitlogic.com
 */

package org.adroitlogic.ultraesb.rest;

import junit.framework.Assert;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for the path pattern feature of the HTTP transport for REST services to extract values from the HTTP path
 *
 * @author Ruwan
 * @since 2.2.0
 */
public class HttpPathPatternTest {

    private static final Logger logger = LoggerFactory.getLogger(HttpPathPatternTest.class);

    private static final String REST_URL = "http://localhost:8280/service/accountDetails/1234/account/456";
    private static final DefaultHttpClient client = new DefaultHttpClient();

    @BeforeClass
    public static void setup() {
        UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-116.xml"});
    }

    @Test
    public void testPathParam() throws Exception {

        // Get the new customer
        logger.debug("*** GET the account detail echo **");
        HttpGet httpget = new HttpGet(REST_URL);
        HttpResponse response = client.execute(httpget);
        String payload = EntityUtils.toString(response.getEntity());
        logger.debug("Received payload is : {}", payload);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertTrue(payload.contains("<customerId>1234</customerId>"));
        Assert.assertTrue(payload.contains("<accountId>456</accountId>"));
    }

    @AfterClass
    public static void tearDown() {
        ServerManager.getInstance().shutdown();
    }
}
