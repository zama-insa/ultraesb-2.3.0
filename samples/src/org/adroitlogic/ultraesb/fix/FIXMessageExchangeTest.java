/*
* AdroitLogic UltraESB Enterprise Service Bus
*
* Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
*
* GNU Affero General Public License Usage
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
* Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
* any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
* warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for
* more details.
*
* You should have received a copy of the GNU Affero General Public License along with this program (See LICENSE-AGPL.TXT).
* If not, see http://www.gnu.org/licenses/agpl-3.0.html
*
* Commercial Usage
*
* Licensees holding valid UltraESB Commercial licenses may use this file in accordance with the UltraESB Commercial
* License Agreement provided with the Software or, alternatively, in accordance with the terms contained in a written
* agreement between you and AdroitLogic.
*
* If you are unsure which license is appropriate for your use, or have questions regarding the use of this file,
* please contact AdroitLogic at info@adroitlogic.com
*/

package org.adroitlogic.ultraesb.fix;

import junit.framework.Assert;
import org.adroitlogic.ultraesb.ServerManager;
import org.adroitlogic.ultraesb.UltraServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * @author Amila Paranawithana
 */
public class FIXMessageExchangeTest {

    private final static Logger logger = LoggerFactory.getLogger(FIXMessageExchangeTest.class);
    static final String tmpPath = System.getProperty("java.io.tmpdir");

    // Test message receiver
    private static String acceptorSettings =
        "[default]\n" +
            "FileStorePath=<file_path>\n" +
            "FileLogPath=<file_path>\n" +
            "ConnectionType=acceptor\n" +
            "StartTime=00:00:00\n" +
            "EndTime=23:00:00\n" +
            "HeartBtInt=30\n" +
            "ValidOrderTypes=1,2,F\n" +
            "SenderCompID=EXEC\n" +
            "TargetCompID=UESB\n" +
            "UseDataDictionary=N\n" +
            "DefaultMarketPrice=12.30\n" +
            "\n" +
            "[session]\n" +
            "BeginString=FIX.4.2\n" +
            "SocketAcceptPort=9879";

    //Test message sender
    private static String initiatorSettings =
        "[default]\n" +
            "FileStorePath=<file_path>\n" +
            "FileLogPath=<file_path>\n" +
            "ConnectionType=initiator\n" +
            "TargetCompID=UESB\n" +
            "SocketConnectHost=localhost\n" +
            "StartTime=00:00:00\n" +
            "EndTime=23:00:00\n" +
            "HeartBtInt=30\n" +
            "ReconnectInterval=5\n" +
            "SocketConnectPort=12000\n" +
            "\n" +
            "[session]\n" +
            "BeginString=FIX.4.2\n" +
            "SenderCompID=BANZAI";

    private static final Symbol SYMBOL = new Symbol();
    private static final MsgType MSG_TYPE = new MsgType();
    private static final OrderQty ORDER_QTY = new OrderQty();
    static Message initiatorReceivedMessage;
    static Message acceptorReceivedMessage;
    static boolean isReceived;
    private static SocketInitiator socketInitiator = null;
    private static SocketAcceptor socketAcceptor = null;
    private static File fixTest;

    @BeforeClass
    public static void setUp() throws Exception {

        createTempFile();

        UltraServer.main(new String[]{"--confDir=conf", "--rootConf=samples/conf/ultra-sample-" + 550 + ".xml"});

        String tempPath = tmpPath + "/fixTest";
        acceptorSettings = acceptorSettings.replaceAll("<file_path>", tempPath);
        initiatorSettings = initiatorSettings.replaceAll("<file_path>", tempPath);

        //starting acceptor
        InputStream acceptorIs = new ByteArrayInputStream(acceptorSettings.getBytes());
        SessionSettings executorSettings = new SessionSettings(acceptorIs);
        Application application = new TestAcceptorApp();
        FileStoreFactory accepFileStoreFactory = new FileStoreFactory(executorSettings);
        MessageFactory accepMessageFactory = new DefaultMessageFactory();
        FileLogFactory accepFileLogFactory = new FileLogFactory(executorSettings);
        socketAcceptor = new SocketAcceptor(application, accepFileStoreFactory, executorSettings, accepFileLogFactory, accepMessageFactory);
        socketAcceptor.start();  // ready to accept connections from ultraEsb
        logger.info("Test acceptor started with settings: {}", socketAcceptor);

        //starting initiator
        InputStream initiatorIs = new ByteArrayInputStream(initiatorSettings.getBytes());
        SessionSettings initiatorSettings = new SessionSettings(initiatorIs);
        Application initiatorApplication = new TestInitiatorApp();
        FileStoreFactory initFileStoreFactory = new FileStoreFactory(initiatorSettings);
        FileLogFactory initFileLogFactory = new FileLogFactory(initiatorSettings);
        MessageFactory initMessageFactory = new DefaultMessageFactory();
        socketInitiator = new SocketInitiator(initiatorApplication, initFileStoreFactory, initiatorSettings, initFileLogFactory, initMessageFactory);
        socketInitiator.start();
        logger.info("Test initiator started with settings: {}", initiatorSettings);

        for (SessionID sessionID : socketInitiator.getSessions()) {
            Session.lookupSession(sessionID).logon();
        }
    }

    /**
     * This is the test for receiving fix message from a fix endpoint, processing it at proxy service and forwarding
     * to the target fix endpoint.
     */
    @Test
    public void testSendingFIXMessages() throws Exception {
        NewOrderSingle newOrderSingle = bookSingleOrder(socketInitiator.getSessions().get(0));
        boolean isTrue = true;
        while (isTrue) {
            if (isReceived) {
                Message.Header acceptorHeader = acceptorReceivedMessage.getHeader();
                Message.Header sentMsgHeader = newOrderSingle.getHeader();
                Assert.assertEquals(acceptorHeader.getField(MSG_TYPE).getValue(), sentMsgHeader.getField(MSG_TYPE).getValue());
                Assert.assertEquals(acceptorReceivedMessage.getField(SYMBOL).getValue(), newOrderSingle.getField(SYMBOL).getValue());
                Assert.assertEquals(acceptorReceivedMessage.getField(ORDER_QTY).getValue(), newOrderSingle.getField(ORDER_QTY).getValue());
                isTrue = false;
            }
            Thread.sleep(1000);
        }
        deleteTempFile(fixTest);
    }

    /**
     * Creates a new Single order of version 4.2 and send to ESB proxy over session created with ESB.
     *
     * @param sessionID identifier of the order sending session
     * @return NewSingleOrder
     */
    private NewOrderSingle bookSingleOrder(SessionID sessionID) {
        ClOrdID orderId = new ClOrdID("1");
        HandlInst instruction = new HandlInst(HandlInst.AUTOMATED_EXECUTION_ORDER_PRIVATE);
        String symbol = "EUR/USD" + Math.random();
        Symbol mainCurrency = new Symbol(symbol);
        Side side = new Side(Side.BUY);
        TransactTime transactionTime = new TransactTime();
        OrdType orderType = new OrdType(OrdType.FOREX_MARKET);
        NewOrderSingle newOrderSingle = new NewOrderSingle(orderId, instruction, mainCurrency, side, transactionTime, orderType);
        newOrderSingle.set(new OrderQty(Math.random() * 1000));
        try {
            Session.sendToTarget(newOrderSingle, sessionID);
            logger.info("Sent fix order message over session: {}", sessionID.toString());
            return newOrderSingle;
        } catch (SessionNotFound e) {
            logger.error("Could not send the fix order message over sessionID: {}", sessionID, e);
            return null;
        }
    }

    /**
     * Create required fix storage files use by quickfix/j
     */
    private static void createTempFile() {
        fixTest = new File( tmpPath+ "/fixTest");
        if (!fixTest.exists()) {
            fixTest.mkdir();
        } else {
           deleteTempFile(fixTest);
           createTempFile();
        }
    }

    /**
     * Deleting the file created
     */
    private static void deleteTempFile(File file) {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    deleteTempFile(fileDelete);
                }
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (socketInitiator != null) {
            socketInitiator.stop(true);
        }
        if (socketAcceptor != null) {
            socketAcceptor.stop(true);
        }
        ServerManager.getInstance().shutdown(3000);
    }
}
