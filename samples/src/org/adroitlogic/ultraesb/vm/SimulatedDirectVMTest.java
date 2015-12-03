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

import org.adroitlogic.ultraesb.AbstractUTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * A test case that simulates a received message and calls the VM transport
 *
 * @author Ruwan
 * @since 2.3.0
 */
public class SimulatedDirectVMTest extends AbstractUTestCase {

    @BeforeClass
    public static void setup() {
        startESB(421);
    }

    @Test
    public void testDirectObject() {
        int initVal = 200;
        MessageContent response = invokeViaVM(MessageContent.forObject(new TestBean(initVal)), "vm-direct", 120000L);
        assertEquals("<response><init>" + initVal + "</init><gen>" + (initVal + 100) + "</gen></response>", response.toString());
    }

    @AfterClass
    public static void tearDown() {
        stopESB();
    }
}
