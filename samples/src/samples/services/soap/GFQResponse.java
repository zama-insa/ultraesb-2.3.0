/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.soap;

public class GFQResponse {

    private static final int COUNT = 365;
    TradingDay[] tradeHistory = null;

    public GFQResponse() {
    }

    public GFQResponse(String symbol) {
        tradeHistory = new TradingDay[COUNT];
        for (int i=0; i<COUNT; i++) {
            tradeHistory[i] = new TradingDay(i, new GQResponse(symbol));
        }
    }

    public TradingDay[] getTradeHistory() {
        return tradeHistory;
    }

    public void setTradeHistory(TradingDay[] tradeHistory) {
        this.tradeHistory = tradeHistory;
    }

}
