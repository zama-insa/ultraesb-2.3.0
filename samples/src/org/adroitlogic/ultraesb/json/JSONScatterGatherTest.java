/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.json;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;

/**
 * Tests the split aggregate of JSON
 *
 * @author Ruwan
 * @since 2.1.0
 */
public class JSONScatterGatherTest extends UTestCase {

    private static final String scatterGatherURL = "http://localhost:8280/service/json-scatter-gather";

    public static Test suite() {
        return new UTestSetup(JSONScatterGatherTest.class, 222);
    }

    public void testScatterGather() throws Exception {

        HttpPost httpPost = new HttpPost(scatterGatherURL);
        httpPost.setEntity(new StringEntity(FileUtils.readFileToString(
                new File("samples/resources/requests/1K_order.json")), APPLICATION_JSON));
        HttpResponse response = client.execute(httpPost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String jsonResponse = EntityUtils.toString(response.getEntity());
        Assert.assertEquals(jsonResponse.split("orderId").length, 4);
    }
}
