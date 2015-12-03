/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.hl7;

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.adroitlogic.ultraesb.api.*;
import org.adroitlogic.ultraesb.api.transport.amqp.AMQPConstants;

/**
 * Incoming message mediation sequence for the HL7 to AMQP sample.
 * Please see the sample ultra-sample-752.xml
 *
 * @author Ruwan
 * @since 2.0.0
 */
public class HL72AMQPSampleInSequence extends AbstractJavaSequence {

    private static final Logger logger = LoggerFactory.getLogger(HL72AMQPSampleInSequence.class);

    @Override
    public void execute(Message message, Mediation mediation) throws Exception {

        /* all the info logs are really debug logs, however for the PoC they are logged at info level to easily
            understand the flow */
        logger.info("Received an HL7 message to the UltraESB, tracing the message payload..");
        logger.trace(mediation.getHL7Support().readPayloadAsString(message));

        logger.info("Archiving the received file into the file system under /tmp/hl7/adt/");
        mediation.sendToEndpoint(message, "file-archive");

        try {
            /* uncomment to stop relaying the inbound HL7 transport headers into the outbound AMQP endpoint */
            // message.getTransportHeaders().clear();
            message.addTransportHeader("app-id", "UltraESB-2.0.0");
            message.addTransportHeader("type", "hl7");
            message.addTransportHeader("message-id", message.getMessageUUID().toString());
            mediation.sendToEndpoint(message, "amqp-destination");

            /* if the routing_key needs to be dynamic, then use the following code instead of the above line
                given that the message property "amqp_routing_key" is set previously */
            // message.addMessageProperty(AMQPConstants.EXCHANGE, "amq.topic");
            // message.setDestinationURL("amqp:/" + message.getStringMessageProperty("amqp_routing_key"));
            // mediation.sendToEndpoint(message, "default");

            logger.info("Delivered the message into AMQP exchange {}",
                    message.getStringMessageProperty(AMQPConstants.EXCHANGE));
        } catch (Exception e) {
            logger.error("Error in delivering the message to the amqp exchange {} with routing key {}",
                    message.getStringMessageProperty(AMQPConstants.EXCHANGE), message.getDestinationURL(), e);
            /* this line will result in the error handler sequence to be triggered and the message to be archived
                into the failure directory, this can be extended such that the messages stored in the failure archive to
                be re-tried later by using a scheduled file listener to read the failure archive and re-inject the
                messages into the AMQP exchange */
            throw new BusRuntimeException("Error delivering the message to the amqp exchange "
                    + message.getStringMessageProperty(AMQPConstants.EXCHANGE), e);
        }

        /* send the HL7 ack response */
        Message res = mediation.getHL7Support().createAckResponse(message);
        logger.info("Sending the HL7 ACK response");
        mediation.sendResponse(res, 0);
    }

    // Note that these lifecycle management methods can be completely omitted, Putting these just for illustration
    // purposes of the lifecycle management of a sequence
    @Override
    public void init(Configuration config) {
        super.init(config);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}