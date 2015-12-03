/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.http;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.IOException;

public class TextServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/plain");
            out = response.getWriter();
            out.println(REPLY);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static String REPLY =
        "Plain text Hello World";
}
