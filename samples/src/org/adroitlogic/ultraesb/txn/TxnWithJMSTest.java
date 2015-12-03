/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.txn;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.File;
import java.util.Hashtable;

/**
 * @author asankha
 */
public class TxnWithJMSTest extends UTestCase {

    private static BrokerService broker = null;

    public static Test suite() {
        return new TestSetup(new TestSuite(TxnWithJMSTest.class)) {

            protected void setUp() throws Exception {

                broker = new BrokerService();
                broker.setPersistent(false);
                broker.addConnector("tcp://localhost:61616");
                broker.setDataDirectory("build/amq/data");
                broker.setTmpDataDirectory(new File("build/amq/tmp"));
                broker.start();
                logger.info("ActiveMQ Broker Started...");

                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-701.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                broker.stop();
            }
        };
    }

    public void testTransactions() throws Exception {
        Hashtable<String, String> props = new Hashtable<String, String>();
        props.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.put("java.naming.provider.url", "tcp://localhost:61616");
        Context jndiContext = new InitialContext(props);

        ConnectionFactory connectionFactory = (ConnectionFactory) jndiContext.lookup("QueueConnectionFactory");
        Destination jmsProxyIN = (Destination) jndiContext.lookup("dynamicQueues/jmsProxyIN");
        Destination jmsProxyOUT = (Destination) jndiContext.lookup("dynamicQueues/jmsProxyOUT");

        Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageProducer producer = session.createProducer(jmsProxyIN);

        TextMessage m = session.createTextMessage();
        m.setText("adroitlogic");
        producer.send(m);
        connection.close();

        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TextMessage response = (TextMessage) session.createConsumer(jmsProxyOUT).receive(2000);

        if (response != null) {
            Assert.assertEquals("** ECHO ** adroitlogic", response.getText());
        } else {
            fail("Did not receive the echoed message back from the proxy");
        }
        connection.close();
    }
}
