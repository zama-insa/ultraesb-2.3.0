/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.jms;

import junit.extensions.TestSetup;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import samples.services.restful.shop.domain.Customer;

import javax.jms.*;

/**
 * @author asankha
 */
public class JMSMessageTest extends UTestCase {

    public static Test suite() {
        return new TestSetup(new TestSuite(JMSMessageTest.class)) {
            protected void setUp() throws Exception {
                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-509.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
            }
        };
    }

    public void testStreamMessages() throws Exception {

        ActiveMQConnectionFactory amqCF = new ActiveMQConnectionFactory("vm://localhost");
        Connection con = amqCF.createConnection();
        con.start();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        StreamMessage sMsg = session.createStreamMessage();
        sMsg.writeBoolean(true);
        sMsg.writeByte((byte) 10);
        sMsg.writeBytes("hello worldxx".getBytes(), 0, 11);
        sMsg.writeChar('c');
        sMsg.writeDouble(2.34d);
        sMsg.writeFloat(4.32f);
        sMsg.writeInt(43);
        sMsg.writeLong(34392212L);
        sMsg.writeObject(54);
        sMsg.writeShort((short) 4);
        sMsg.writeString("asankha");

        ActiveMQQueue dest = new ActiveMQQueue("Q.STREAM-REQ");
        MessageProducer p = session.createProducer(dest);
        p.send(sMsg);

        dest = new ActiveMQQueue("Q.STREAM-RES");
        MessageConsumer c = session.createConsumer(dest);
        Message m = c.receive(2000);

        Assert.assertNotNull(m);
        Assert.assertTrue(m instanceof StreamMessage);
        StreamMessage sm = (StreamMessage) m;

        Assert.assertEquals(true, sm.readBoolean());
        Assert.assertEquals(10, sm.readByte());
        byte[] b = new byte[11];
        sm.readBytes(b);
        Assert.assertEquals("hello world", new String(b));
        Assert.assertEquals('c', sm.readChar());
        Assert.assertEquals(2.34d, sm.readDouble());
        Assert.assertEquals(4.32f, sm.readFloat());
        Assert.assertEquals(43, sm.readInt());
        Assert.assertEquals(34392212L, sm.readLong());
        Assert.assertEquals(54, sm.readObject());
        Assert.assertEquals(4, sm.readShort());
        Assert.assertEquals("asankha", sm.readString());
        con.close();
    }

    public void testObjectMessages() throws Exception {
        ActiveMQConnectionFactory amqCF = new ActiveMQConnectionFactory("vm://localhost");
        Connection con = amqCF.createConnection();
        con.start();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Customer cust = new Customer();
        cust.setLastName("Perera");
        cust.setId(1234);
        cust.setFirstName("Asankha");
        ObjectMessage oMsg = session.createObjectMessage(cust);
        Destination dest = new ActiveMQQueue("Q.OBJECT-REQ");
        MessageProducer p = session.createProducer(dest);
        p.send(oMsg);

        dest = new ActiveMQQueue("Q.OBJECT-RES");
        MessageConsumer c = session.createConsumer(dest);
        Message m = c.receive(2000);

        Assert.assertNotNull(m);
        Assert.assertTrue(m instanceof ObjectMessage);
        ObjectMessage om = (ObjectMessage) m;
        cust = (Customer) om.getObject();
        Assert.assertEquals("Avanka", cust.getFirstName());
        Assert.assertEquals(5678, cust.getId());
        con.close();
    }

    public void testTextMessages() throws Exception {
        ActiveMQConnectionFactory amqCF = new ActiveMQConnectionFactory("vm://localhost");
        Connection con = amqCF.createConnection();
        con.start();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        TextMessage oMsg = session.createTextMessage("hello-world");
        Destination dest = new ActiveMQQueue("Q.TEXT-REQ");
        MessageProducer p = session.createProducer(dest);
        p.send(oMsg);

        dest = new ActiveMQQueue("Q.TEXT-RES");
        MessageConsumer c = session.createConsumer(dest);
        Message m = c.receive(2000);

        Assert.assertNotNull(m);
        Assert.assertTrue(m instanceof TextMessage);
        TextMessage tm = (TextMessage) m;
        Assert.assertEquals("response-hello-world", tm.getText());
        con.close();
    }

    public void testBytesMessages() throws Exception {
        ActiveMQConnectionFactory amqCF = new ActiveMQConnectionFactory("vm://localhost");
        Connection con = amqCF.createConnection();
        con.start();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        BytesMessage bMsg = session.createBytesMessage();
        bMsg.writeBytes("hello-world".getBytes());
        Destination dest = new ActiveMQQueue("Q.BYTES-REQ");
        MessageProducer p = session.createProducer(dest);
        p.send(bMsg);

        dest = new ActiveMQQueue("Q.BYTES-RES");
        MessageConsumer c = session.createConsumer(dest);
        Message m = c.receive(2000);

        Assert.assertNotNull(m);
        Assert.assertTrue(m instanceof BytesMessage);
        BytesMessage bm = (BytesMessage) m;
        byte[] bytes = new byte[(int) bm.getBodyLength()];
        bm.readBytes(bytes);

        Assert.assertEquals("hello-world", new String(bytes));
        con.close();
    }

    public void testMapMessages() throws Exception {
        ActiveMQConnectionFactory amqCF = new ActiveMQConnectionFactory("vm://localhost");
        Connection con = amqCF.createConnection();
        con.start();
        Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

        MapMessage mMsg = session.createMapMessage();
        mMsg.setString("name", "asankha");
        mMsg.setInt("age", 36);
        Destination dest = new ActiveMQQueue("Q.MAP-REQ");
        MessageProducer p = session.createProducer(dest);
        p.send(mMsg);

        dest = new ActiveMQQueue("Q.MAP-RES");
        MessageConsumer c = session.createConsumer(dest);
        Message m = c.receive(2000);

        Assert.assertNotNull(m);
        Assert.assertTrue(m instanceof MapMessage);
        MapMessage map = (MapMessage) m;
        Assert.assertEquals(36, map.getInt("age"));
        Assert.assertEquals("perera", map.getString("name"));
        con.close();
    }
}
