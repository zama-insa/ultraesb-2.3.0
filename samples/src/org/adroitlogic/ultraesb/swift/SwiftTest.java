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
 * You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE.AGPL).
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

package org.adroitlogic.ultraesb.swift;

import junit.framework.Assert;
import org.adroitlogic.ultraesb.api.Environment;
import org.adroitlogic.ultraesb.api.mediation.SwiftSupport;
import org.adroitlogic.ultraesb.core.ConfigurationImpl;
import org.adroitlogic.ultraesb.core.MediationImpl;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.adroitlogic.ultraesb.core.ProxyService;
import org.adroitlogic.ultraesb.core.deployment.DeploymentUnit;
import org.adroitlogic.ultraesb.core.format.SwiftMTMessage;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Sajith Dilshan
 */
public class SwiftTest {

    private static ProxyService ps;
    private static SwiftSupport swiftSupport;

    private static final String messageBlock4 = ":20:2000001305\r\n" +
            ":32A:001031LKR9101,90\r\n" +
            ":50: Corporate Name\r\n" +
            "BOX 12345\r\n" +
            "Colombo\r\n" +
            "Lanka Company XYZ\r\n" +
            ":52A:SAERSARI\r\n" +
            ":53B:/106202529\r\n" +
            ":57A:AAALLKRI\r\n" +
            ":59:/01313078585009\r\n" +
            "Corporate Name\r\n" +
            "BOX 12345\r\n" +
            "Colomo\r\n" +
            "Corporate Name\r\n" +
            ":70:INV/0000000899";

    private static final String mt564Message = "{1:F01CDPLSGS0AXXX0000000000}{2:O5640906010101IMBIMCXXXXXX00000000001309110906N}{4:\r\n" +
            ":16R:GENL\r\n" +
            ":20C::CORP//SG131231CAPD39NZ\r\n" +
            ":20C::SEME//VXO4AHCDFBO3YSMF\r\n" +
            ":20C::COAF//SG131231CAPD39NZ\r\n" +
            ":23G:NEWM\r\n" +
            ":22F::CAEP//DISN\r\n" +
            ":22F::CAEV//CAPD\r\n" +
            ":22F::CAMV//MAND\r\n" +
            ":98C::PREP//20131231122446\r\n" +
            ":25D::PROC//COMP\r\n" +
            ":16S:GENL\r\n" +
            ":16R:USECU\r\n" +
            ":35B:ISIN US233048AC15\r\n" +
            "/SG/7NFB\r\n" +
            "DBS BANK USD900MF210715A\r\n" +
            ":16R:FIA\r\n" +
            ":94B::PLIS//EXCH/N/A\r\n" +
            ":12A::CLAS//Bonds/Notes\r\n" +
            ":16S:FIA\r\n" +
            ":16R:ACCTINFO\r\n" +
            ":97C::SAFE//GENR\r\n" +
            ":16S:ACCTINFO\r\n" +
            ":16S:USECU\r\n" +
            ":16R:CADETL\r\n" +
            ":98A::ANOU//20131231\r\n" +
            ":98A::XDTE//20131231\r\n" +
            ":98C::RDTE//20140103170000\r\n" +
            ":16S:CADETL\r\n" +
            ":16R:CAOPTN\r\n" +
            ":13A::CAON//001\r\n" +
            ":22F::CAOP//CASH\r\n" +
            ":11A::OPTN//SGD\r\n" +
            ":17B::DFLT//Y\r\n" +
            ":16R:CASHMOVE\r\n" +
            ":22H::CRDB//CRED\r\n" +
            ":98A::PAYD//20140110\r\n" +
            ":92F::GRSS//SGD0,25\r\n" +
            ":92F::NETT//SGD0,25\r\n" +
            ":16S:CASHMOVE\r\n" +
            ":16S:CAOPTN\r\n" +
            ":16R:ADDINFO\r\n" +
            ":70E::TXNR//FNLY/20131231\r\n" +
            ":16S:ADDINFO\r\n" +
            "-}";


    @BeforeClass
    public static void setUp() throws Exception {
        MediationImpl.initialize(new ConfigurationImpl());
        ps = new ProxyService();
        ConfigurationImpl config = new ConfigurationImpl();
        config.setEnvironment(new Environment("unit_test"));
        ps.init(new DeploymentUnit("default", config, null));
        swiftSupport = MediationImpl.getInstance().getSwiftSupport();
    }

    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

    @Test
    @PerfTest(invocations = 200, threads = 5)
    public void validateMessageTest() {
        MessageImpl message  = new MessageImpl(true, ps, "null");
        message.setCurrentPayload(new SwiftMTMessage(mt564Message));
        swiftSupport.validateMessage(message, true);
        SwiftMTMessage msg = (SwiftMTMessage) message.getCurrentPayload();
        Assert.assertTrue(msg.isValidated());
    }

    @Test
    @PerfTest(invocations = 200, threads = 5)
    public void validateMessageWithTypeTest() {
        MessageImpl message  = new MessageImpl(true, ps, "null");
        message.setCurrentPayload(new SwiftMTMessage(mt564Message));
        swiftSupport.validateMessageWithType(message, true, 564);
        SwiftMTMessage msg = (SwiftMTMessage) message.getCurrentPayload();
        Assert.assertTrue(msg.isValidated());
    }

    @Test
    @PerfTest(invocations = 200, threads = 5)
    public void getFieldValueTest() {
        MessageImpl message  = new MessageImpl(true, ps, "null");
        message.setCurrentPayload(new SwiftMTMessage(mt564Message));
        Assert.assertEquals(swiftSupport.getFieldValue(message, "98C"), ":PREP//20131231122446");
        Assert.assertEquals(swiftSupport.getFieldValue(message, "20C"), ":CORP//SG131231CAPD39NZ");
    }

    @Test
    @PerfTest(invocations = 200, threads = 5)
    public void validateMessageBlock4Test() {
        Assert.assertTrue(swiftSupport.validateMessageBlock4(messageBlock4, 100, false));
    }
}
