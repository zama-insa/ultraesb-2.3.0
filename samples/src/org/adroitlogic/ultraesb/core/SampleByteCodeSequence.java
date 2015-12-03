/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import org.adroitlogic.ultraesb.api.JavaClassSequence;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.Message;
import org.adroitlogic.ultraesb.api.Configuration;

/**
 * @author asankha
 */
public class SampleByteCodeSequence implements JavaClassSequence {
    private long startTime;
    private int count;

    public void execute(Message msg, Mediation mediation) throws Exception {
        System.out.println("Message target :  " + msg.getDestinationURL());
        if ("gold".equals(msg.getFirstTransportHeader("ClientID"))) {
            mediation.sendToEndpoint(msg, "test1");
        } else {
            mediation.sendToEndpoint(msg, "test2");
        }
        count++;
    }

    public void init(Configuration config) {
        startTime = System.currentTimeMillis();
    }

    public void destroy() {
        System.out.println("Byte code sequence, Execution time : " + (System.currentTimeMillis() - startTime) +
            "ms. Processed : " + count + " messages");
    }
}
