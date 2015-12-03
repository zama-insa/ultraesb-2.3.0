/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import junit.framework.Assert;
import org.adroitlogic.metrics.api.alert.*;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UltraServer;
import org.adroitlogic.ultraesb.api.management.AlertActionDefinition;
import org.adroitlogic.ultraesb.api.management.AlertConfigurationDefinition;
import org.adroitlogic.ultraesb.api.management.ServerAdminImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing the dynamic alert configuration functionality
 *
 * @author Ruwan
 * @since 2.0.0
 */
public class DynamicAlertConfigurationTest {

    @Before
    public void prepareServerManager() {
        UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-101.xml"});
    }

    @After
    public void shutdownServerManager() {
        ServerManager.getInstance().shutdown(3000);
    }

    @Test
    public void testDynamicAlertConfig() {
        List<AlertActionDefinition> actions = new ArrayList<AlertActionDefinition>();
        AlertActionDefinition actionDef = new AlertActionDefinition(NotificationAction.class);
        actionDef.addProperty("protocol", NotificationAction.Protocol.EMAIL.name());
        actionDef.addProperty("address", "ruwan@adroitlogic.com");
        actions.add(actionDef);
        ServerAdminImpl.getInstance().defineNonPersistedAlertConfig(new AlertConfigurationDefinition(null, "description",
                "stream", AlertConfigurationDefinition.Severity.WARNING, AlertConfigurationDefinition.Function.AVERAGE,
                1.0D, 1.0D, 0, 0L, actions));
        AlertConfigRegistry reg = ServerManager.getInstance().getConfig().getRootDeploymentUnit().getMetricsEngine().getAlertConfigRegistry();
        Assert.assertEquals(reg.listStream("stream").size(), 1);
        AlertConfig config = reg.listStream("stream").iterator().next();
        Assert.assertEquals(config.getStream(), "stream");
        Assert.assertEquals(config.getDescription(), "description");
        Assert.assertEquals(config.getSeverity(), Severity.WARNING);
        Assert.assertEquals(config.getFunction(), Function.AVERAGE);
        Assert.assertEquals(config.getCriteria().getMaxThreshold(), 1.0D);
        Assert.assertEquals(config.getCriteria().getMinThreshold(), 1.0D);
        Assert.assertEquals(config.retrieveActions().size(), 1);
        AlertAction action = config.retrieveActions().get(0);
        Assert.assertTrue(action instanceof NotificationAction);
        NotificationAction notifyAction = (NotificationAction) action;
        Assert.assertEquals(notifyAction.getProtocol(), NotificationAction.Protocol.EMAIL);
        Assert.assertEquals(notifyAction.getAddress(), "ruwan@adroitlogic.com");
    }
}
