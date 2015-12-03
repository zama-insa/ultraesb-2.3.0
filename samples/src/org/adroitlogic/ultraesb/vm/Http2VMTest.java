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

package org.adroitlogic.ultraesb.vm;

import org.junit.Assert;
import org.adroitlogic.ultraesb.AbstractUTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case for switching from HTTP to VM transport
 *
 * @author Ruwan
 * @since 2.3.0
 */
public class Http2VMTest extends AbstractUTestCase {

    @BeforeClass
    public static void setup() {
        startESB(420);
        startSampleServers();
    }

    @Test
    public void serviceCallOverHTTPViaVMTransportExternalResponse() {
        // first invoke the echo-proxy directly from HTTP
        invokeWithSuccessCheck("echo-proxy");
        // then invoke the vm-echo-proxy that should in-turn call the above same echo-proxy service via vm which
        // again calls an HTTP endpoint
        String replyPayload = invokeWithSuccessCheck("vm-echo-proxy");
        logger.info("Final reply is : {}", replyPayload);
        Assert.assertEquals(getQuoteRequest, replyPayload);
    }

    @Test
    public void serviceCallOverHTTPViaVMTransportDirectResponse() {
        // first invoke the echo-back directly from HTTP
        invokeWithSuccessCheck("echo-back");
        // then invoke the vm-echo-back that should in-turn call the above same echo-back service via vm
        String replyPayload = invokeWithSuccessCheck("vm-echo-back");
        logger.info("Final reply is : {}", replyPayload);
    }

    @AfterClass
    public static void cleanup() {
        stopSampleServers();
        stopESB();
    }
}
