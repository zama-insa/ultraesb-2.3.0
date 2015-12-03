/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import samples.services.hessiantest.ArrayMultiplicationHessian;

/**
 * A sample jetty server that is used to host sample services for testing purposes
 *
 * @author asankha
 * @author Ruwan
 */
public class SampleServer {

    public static void main(String[] args) throws Exception {

        Server server = new Server(9000);
        server.addConnector(JettyUtils.sampleSslConnector());

        ServletContextHandler context = new ServletContextHandler(server, "/hessiantest", ServletContextHandler.SESSIONS);
        context.addServlet(ArrayMultiplicationHessian.class, "/hessian-stress-test");
        server.setHandler(context);

        server.setHandler(JettyUtils.sampleWebAppContext());
        server.start();
    }
}
