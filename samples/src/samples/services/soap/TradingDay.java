/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.soap;

public class TradingDay {
    int day = 0;
    GQResponse quote = null;

    public TradingDay() {
    }

    public TradingDay(int day, GQResponse quote) {
        this.day = day;
        this.quote = quote;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public GQResponse getQuote() {
        return quote;
    }

    public void setQuote(GQResponse quote) {
        this.quote = quote;
    }
}
