/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import org.adroitlogic.ultraesb.api.Message;
import org.adroitlogic.ultraesb.api.helper.ResponseValidator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sample response validator to be used as a response validator class in an endpoint
 *
 * @author Ruwan
 * @since 2.0.0
 */
@SuppressWarnings("UnusedDeclaration")
public class SampleResponseValidator implements ResponseValidator {

    public static final AtomicInteger validatedRespCount = new AtomicInteger(0);

    @Override
    public ResponseType validateResponse(Message msg) {
        System.out.println("Validated the response " + validatedRespCount.incrementAndGet()
                + " : " + msg.getCurrentPayload().toString());
        return ResponseType.VALID;
    }
}
