/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import org.adroitlogic.ultraesb.api.Configuration;
import org.adroitlogic.ultraesb.api.JavaClassSequence;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.Message;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample sequence to keep track of the current messages being processed. Used in sample 802.
 *
 * @author Ruwan
 * @since 1.7.2
 */
public class CounterSimulatorSequence implements JavaClassSequence {
    
    private static final AtomicInteger messagesInProcessing = new AtomicInteger();
    
    public void execute(Message msg, Mediation mediation) throws Exception {
        try {
            System.out.println("Message count being processed : " + messagesInProcessing.incrementAndGet());
            Thread.sleep(1000);
            Message res = msg.createDefaultResponseMessage();
            mediation.setPayloadFromString(res, mediation.readPayloadAsString(msg));
            mediation.sendResponse(res, 200);
        } finally {
            messagesInProcessing.decrementAndGet();
        }
    }

    public void init(Configuration config) {}
    public void destroy() {}
}
