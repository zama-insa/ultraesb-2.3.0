/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb;

import org.adroitlogic.ultraesb.core.MessageImpl;
import org.adroitlogic.ultraesb.transport.base.AbstractTransportSender;

/**
 * @author asankha
 */
public class NullTransportSender extends AbstractTransportSender {

    public NullTransportSender() {
        originatingTransport = "null";
    }

    public SendingResult send(MessageImpl message) {
        return SendingResult.SUCCESSFUL;
    }
}
