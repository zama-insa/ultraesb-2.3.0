/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.logging.api.LoggerFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import samples.services.hessian.HessianStockQuoteService;
import samples.services.hessian.StockQuoteService;
import samples.services.soap.GQ;

/**
 * @author asankha
 */
public class HessianTest extends UTestCase {

    private static final Logger logger = LoggerFactory.getLogger(HessianTest.class);

    private static Server server = null;

    public static Test suite() {
        return new TestSetup(new TestSuite(HessianTest.class)) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-301.xml"});

                server = new Server(9000);
                ServletContextHandler context = new ServletContextHandler(server, "/hessian", ServletContextHandler.SESSIONS);
                context.addServlet(HessianStockQuoteService.class, "/hessian-stockquote");
                server.setHandler(context);
                server.start();
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                if (server != null) {
                    server.stop();
                }
            }
        };
    }

    public void testHessiancall() throws Exception {
        String url = "http://localhost:8280/service/hessian-proxy";
        HessianProxyFactory factory = new HessianProxyFactory();
        StockQuoteService svc = (StockQuoteService) factory.create(StockQuoteService.class, url);
        GQ gq = new GQ();
        gq.setSymbol("ADRT");
        logger.info("Last trade : " + svc.getQuote(gq).getLast());
    }
}
