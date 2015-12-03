/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.json;

import org.adroitlogic.ultraesb.api.Message;
import org.adroitlogic.ultraesb.api.format.MessageFormat;
import org.adroitlogic.ultraesb.core.format.StringMessage;

import java.util.Collection;
import java.util.List;

/**
 * Sample class to merge Json messages as raw strings as { "merged" : [<raw-json-1>, <raw-json-2>, ... <raw-json-n>] }
 *
 * Picks source payloads to merge from the attachments of the current message, and writes the 'merged' message as the
 * current payload
 * 
 * @author asankha
 */
public class JsonMerge {

    public static void aggregateAsJsonResult(Message msg) {

        StringBuilder sb = new StringBuilder();
        sb.append("{ \"merged\": [");

        int size = msg.getAttachments().values().size();
        int count = 1;

        for (MessageFormat att : msg.getAttachments().values()) {
            sb.append(att.toString());
            if (count++ < size) {
                sb.append(",");
            }
        }
        sb.append("]}");
        msg.setCurrentPayload(new StringMessage(sb.toString()));
    }
}
