/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.hl7;

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.api.*;

/**
 * Sample error handler sequence for the sample 752
 *
 * @author Ruwan
 * @since 2.0.0
 */
public class SampleErrorHandler extends AbstractJavaSequence {

    private static final Logger logger = LoggerFactory.getLogger(SampleErrorHandler.class);

    @Override
    public void execute(Message message, Mediation mediation) throws Exception {
        logger.error("Unhandled error in processing the HL7 message with message id {}", message.getMessageUUID());
        mediation.sendToEndpoint(message, "failure-archive");
    }
}