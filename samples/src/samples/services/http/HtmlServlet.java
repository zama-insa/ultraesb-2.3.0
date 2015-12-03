/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.http;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Map;

public class HtmlServlet extends HttpServlet {

    @SuppressWarnings({"unchecked"})
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html");
            out = response.getWriter();

            StringBuilder sb = new StringBuilder();
            sb.append(REPLY_PREFIX);

            sb.append("<p>Method: " + request.getMethod() + "</p>");
            sb.append("<p>Request URI: " + request.getRequestURI() + "</p>");
            sb.append("<p>Protocol: " + request.getProtocol() + "</p>");

            Map<String, String[]> params = request.getParameterMap();
            sb.append("<p>Parameters<br/>");
            for (Map.Entry<String, String[]> e : params.entrySet()) {
                sb.append("key: " + e.getKey() + " values:");
                String[] values = e.getValue();
                for (String v : values) {
                    sb.append(" " + v);
                }
                sb.append("<br/>");
            }
            sb.append("</p>");
            sb.append(REPLY_SUFFIX);
            out.print(sb.toString());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final static String REPLY_PREFIX =
        "<html>" +
            "<head>" +
            "   <title>Hello World</title>" +
            "</head>" +
            "<body>" +
            "   <h1>Hello World</h1>";

    private final static String REPLY_SUFFIX =
            "</body>" +
        "</html>";
}
