/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb;

import junit.framework.TestCase;
import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import samples.services.JettyUtils;

import java.util.Arrays;

/**
 * Generic test suite setup that will be used by the UTestCase classes to setup a collection of sample server services
 * and the ESB with the sample configuration
 *
 * @author Ruwan
 * @since 2.1.0
 */
public class MultiServerUTestSetup extends UTestSetup {

    private int[] serverPorts;
    private Server[] servers;

    public MultiServerUTestSetup(Class<? extends TestCase> test, int sampleNumber, int... serverPorts) {
        super(test, sampleNumber, serverPorts[0]);
        this.serverPorts = Arrays.copyOfRange(serverPorts, 1, serverPorts.length);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        servers = new Server[serverPorts.length];
        int i = 0;
        for (int serverPort : serverPorts) {
            Server server = new Server(serverPort);
            server.setHandler(JettyUtils.sampleWebAppContext());
            server.start();
            servers[i++] = server;
        }
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        for (Server server : servers) {
            if (server != null) {
                server.stop();
            }
        }
    }
}
