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

import org.adroitlogic.logging.api.Logger;
import org.adroitlogic.logging.api.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.ExecutionReport;
import quickfix.fix42.NewOrderSingle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Amila Paranawithana
 */
public class TestAcceptorApp extends MessageCracker implements Application {

    private final static Logger logger = LoggerFactory.getLogger(TestAcceptorApp.class);
    private Map<String, Double> priceMap = null;

    public TestAcceptorApp() {
        priceMap = new HashMap<String, Double>();
        priceMap.put("EUR/USD", 1.234);
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#onCreate(quickfix.SessionID)
     */
    @Override
    public void onCreate(SessionID sessionId) {
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#onLogon(quickfix.SessionID)
     */
    @Override
    public void onLogon(SessionID sessionId) {
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#onLogout(quickfix.SessionID)
     */
    @Override
    public void onLogout(SessionID sessionId) {
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#toAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toAdmin(Message message, SessionID sessionId) {
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#fromAdmin(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromAdmin(Message message, SessionID sessionId)
        throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
        RejectLogon {
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#toApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
    }

    /**
     * (non-Javadoc)
     *
     * @see quickfix.Application#fromApp(quickfix.Message, quickfix.SessionID)
     */
    @Override
    public void fromApp(Message message, SessionID sessionId)
        throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
        UnsupportedMessageType {
        crack(message, sessionId);
    }

    public void onMessage(NewOrderSingle message, SessionID sessionID)
        throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        logger.info("FIX message received to endpoint over session: {}", sessionID.toString());
        FIXMessageExchangeTest.acceptorReceivedMessage = message;
        FIXMessageExchangeTest.isReceived = true;
        OrdType orderType = message.getOrdType();
        Symbol currencyPair = message.getSymbol();
        // assuming that we are directly dealing with Market
        Price price = null;
        if (OrdType.FOREX_MARKET == orderType.getValue()) {
            if(this.priceMap.containsKey(currencyPair.getValue())){
                price = new Price(this.priceMap.get(currencyPair.getValue()));
            } else {
                price = new Price(1.4589);
            }
        }

        OrderID orderNumber = new OrderID("1");
        ExecID execId = new ExecID("1");
        ExecTransType exectutionTransactionType = new ExecTransType(ExecTransType.NEW);
        ExecType purposeOfExecutionReport =new ExecType(ExecType.FILL);
        OrdStatus orderStatus = new OrdStatus(OrdStatus.FILLED);
        Side side = message.getSide();
        LeavesQty leavesQty = new LeavesQty(100);
        CumQty cummulativeQuantity = new CumQty(100);
        AvgPx avgPx = new AvgPx(1.235);
        ExecutionReport executionReport = new ExecutionReport(orderNumber,execId, exectutionTransactionType,
            purposeOfExecutionReport, orderStatus, currencyPair, side, leavesQty, cummulativeQuantity, avgPx);
        executionReport.set(price);
        try {
            Session.sendToTarget(executionReport, sessionID);
        } catch (SessionNotFound ignore) { }
    }
}