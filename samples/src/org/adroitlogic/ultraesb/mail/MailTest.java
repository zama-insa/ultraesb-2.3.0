/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.mail;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UltraServer;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * @author asankha
 */
public class MailTest extends UTestCase {

    private static GreenMail greenMail = null;

    public static Test suite() {
        return new TestSetup(new TestSuite(MailTest.class)) {
            protected void setUp() throws Exception {
                greenMail = new GreenMail(ServerSetupTest.ALL);
                greenMail.setUser("user1@localhost", "user1", "pass1");
                greenMail.setUser("user2@localhost", "user2", "pass2");
                greenMail.setUser("user3@localhost", "user3", "pass3");
                greenMail.setUser("sender@localhost", "sender", "sender");
                greenMail.setUser("fwd@localhost", "fwd", "fwd");
                greenMail.start();

                UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-501.xml"});
            }

            protected void tearDown() throws Exception {
                ServerManager.getInstance().shutdown(3000);
                greenMail.stop();
            }
        };
    }

    public void testMailService() throws Exception {

        Session session = Session.getInstance(new Properties());
        MimeMessage m = new MimeMessage(session);
        m.setRecipients(Message.RecipientType.TO, InternetAddress.parse("user1@localhost"));
        m.setFrom(new InternetAddress("from@localhost"));
        m.setHeader("RemoveHeader", "shouldNotSee");
        m.setHeader("AnotherHeaderToRemove", "shouldNotSee");
        m.setSubject("subject1");
        //m.setDataHandler(new DataHandler(new ByteArrayDataSource("message1", "text/plain")));

        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart part1 = new MimeBodyPart();
        part1.setContent("textpart", "text/plain");
        multipart.addBodyPart(part1);
        MimeBodyPart part2 = new MimeBodyPart();
        part2.setContent("xmlpart", "text/xml");
        multipart.addBodyPart(part2);
        m.setContent(multipart);

        greenMail.getManagers().getUserManager().getUser("user1").deliver(m);

        // -------- test proxy 2 ---------------
        GreenMailUtil.sendTextEmailTest("user2@localhost", "from@localhost", "fail", "imap-body");
        Thread.sleep(1000);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        for (MimeMessage msg : messages) {
            //System.out.println("==> Message from : " + msg.getFrom()[0] + " subject : " + msg.getSubject());
            if ("subject-fwd".equals(msg.getSubject()) && "from@localhost".equals(msg.getFrom())) {
                if (!"FAIL".equals(msg.getFolder().getName())) {
                    fail("Expected sent message to be in the folder FAIL after processing exception");
                }
                break;
            }
            Thread.sleep(1000);
        }

        // -------- test proxy 3 ---------------
        GreenMailUtil.sendTextEmailTest("user3@localhost", "from@localhost", "subject-fwd", "imap-body");
        Thread.sleep(1000);

        messages = greenMail.getReceivedMessages();
        int count = 0;
        for (MimeMessage msg : messages) {
            //System.out.println("==> Message from : " + msg.getFrom()[0] + " subject : " + msg.getSubject());
            if ("reply".equals(msg.getSubject()) && "sender@localhost".equals(msg.getFrom())) {
                // we got the reply
                if (++count == 2) {
                    break;
                }
            }
            if ("subject-fwd".equals(msg.getSubject()) && "from@localhost".equals(msg.getFrom())) {
                if (!"DONE".equals(msg.getFolder().getName())) {
                    fail("Expected sent message to be in the folder DONE after processing");
                }
                if (++count == 2) {
                    break;
                }
            }
            Thread.sleep(1000);
        }
    }
}
