/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.json;

import org.adroitlogic.ultraesb.api.Message;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.HashMap;

/**
 * A sample transformation for Json using the Jackson library, and returns the result as a String 
 *
 * Converts messages of the form { "message" : "hello world, I am client N!" .. } into
 * { "prefix_for_<serverName>" : "Hello <serverName> - hello world, I am client N!" .. }
 *
 * @author asankha
 */
public class SampleJsonTransformation {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String transformRequest(Message msg, String serverName) throws Exception {

        HashMap<String,Object> untyped = mapper.readValue(msg.getCurrentPayload().getInputStream(), HashMap.class);

        String message = (String) untyped.get("message");
        untyped.remove("message");
        untyped.put("prefix_for_" + serverName, "Hello " + serverName + " - " + message);

        return mapper.writeValueAsString(untyped);
    }
}
