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

package org.adroitlogic.ultraesb.http;

import junit.framework.Assert;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import junit.framework.Test;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;

/**
 * @auther Malaka Gallage
 * @since 2.2.0
 */
public class HttpURLEncodeTest extends UTestCase{

    public static Test suite() {
        return new UTestSetup(HttpURLEncodeTest.class, 115);
    }

    public void testHttpWithParams() throws Exception {

        //test with a valid request with parameters
        HttpPost httppost = new HttpPost("http://localhost:8280/service/query?param=value");
        HttpResponse response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());


        //test with a valid request with spaces in parameters
        httppost = new HttpPost("http://localhost:8280/service/query?param=value%20with%20space");
        response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());


        //test with a valid request without parameters
        httppost = new HttpPost("http://localhost:8280/service/query");
        response = client.execute(httppost);
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }
}
