/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.http;

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;

public class JsonServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(JsonServlet.class);

    private final ObjectMapper mapper = new ObjectMapper();
    private static final String PREFIX_FOR = "prefix_for_";
    private static final Random rand = new Random();

    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        try {
            // sleep a random bit .. less than a second
            Thread.sleep(rand.nextInt(1200));
            //Thread.sleep(3000);
        } catch (InterruptedException ignore) {}

        try {
            InputStream is = request.getInputStream();
            HashMap<String,Object> untyped = mapper.readValue(is, HashMap.class);
            is.close();

            int v1 = (Integer) untyped.get("v1");
            int v2 = (Integer) untyped.get("v2");
            untyped.put("result", v1*v2);

            for (String key : untyped.keySet()) {
                if (key.startsWith(PREFIX_FOR)) {
                    String myServerName = key.substring(PREFIX_FOR.length());
                    String messageText = (String) untyped.get(key);
                    untyped.put(key, "Welcome to " + myServerName);
                }
            }

            response.setContentType("application/json");
            OutputStream os = response.getOutputStream();
            mapper.writeValue(os, untyped);
            os.close();

        } catch (Exception ignore) {}
    }
}
