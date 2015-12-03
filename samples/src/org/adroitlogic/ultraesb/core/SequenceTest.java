/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UltraServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author asankha
 * @author Ruwan
 */
public class SequenceTest {

    private ConfigurationImpl config;

    @Before
    public void prepareServerManager() {
        UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/resources/test/test_sequences.xml"});
        config = ServerManager.getInstance().getConfig();
    }

    @After
    public void shutdownServerManager() {
        ServerManager.getInstance().shutdown(3000);
    }

    @Test
    public void testSequences() throws Exception {
        for (int i=1; i<8; i++) {
            testSequence(i);
        }
    }

    private void testSequence(int i) throws Exception {

        Sequence seq = config.getRootDeploymentUnit().getSequence("myseq" + i);
        ProxyService ps = new ProxyService();
        ps.init(config.getRootDeploymentUnit());

        MessageImpl m = new MessageImpl(true, ps, "unittest");
        MessageImpl.CURRENT_MESSAGE.set(m);
        m.setWorkManager(config.getDefaultWorkManager());
        m.addTransportHeader("ClientID", "gold");
        m.setDestinationURL("null://service/gold");
        seq.execute(m);

        m = new MessageImpl(true, ps, "unittest");
        MessageImpl.CURRENT_MESSAGE.set(m);
        m.setWorkManager(config.getDefaultWorkManager());
        m.addTransportHeader("ClientID", "normal");
        m.setDestinationURL("null://service/default");
        seq.execute(m);
        seq.undeploy();
    }
}
