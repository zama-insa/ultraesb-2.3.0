/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.soap;

import java.io.Serializable;
import java.util.Date;

public class GQResponse implements Serializable {
    String symbol;
    double last;
    String lastTradeTimestamp;
    double change;
    double open;
    double high;
    double low;
    int volume;
    double marketCap;
    double prevClose;
    double percentageChange;
    double earnings;
    double peRatio;
    String name;

    public GQResponse() {
    }

    public GQResponse(String symbol) {
        this.symbol = symbol;
        this.last = getRandom(100, 0.9, true);
        this.lastTradeTimestamp = new Date().toString();
        this.change = getRandom(3, 0.5, false);
        this.open = getRandom(last, 0.05, false);
        this.high = getRandom(last, 0.05, false);
        this.low = getRandom(last, 0.05, false);
        this.volume = (int) getRandom(10000, 1.0, true);
        this.marketCap = getRandom(10E6, 5.0, false);
        this.prevClose = getRandom(last, 0.15, false);
        this.percentageChange = change / prevClose * 100;
        this.earnings = getRandom(10, 0.4, false);
        this.peRatio = getRandom(20, 0.30, false);
        this.name = symbol + " Company";
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public String getLastTradeTimestamp() {
        return lastTradeTimestamp;
    }

    public void setLastTradeTimestamp(String lastTradeTimestamp) {
        this.lastTradeTimestamp = lastTradeTimestamp;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public double getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(double marketCap) {
        this.marketCap = marketCap;
    }

    public double getPrevClose() {
        return prevClose;
    }

    public void setPrevClose(double prevClose) {
        this.prevClose = prevClose;
    }

    public double getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(double percentageChange) {
        this.percentageChange = percentageChange;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(double peRatio) {
        this.peRatio = peRatio;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private static double getRandom(double base, double varience, boolean onlypositive) {
        double rand = Math.random();
        return (base + ((rand > 0.5 ? 1 : -1) * varience * base * rand))
            * (onlypositive ? 1 : (rand > 0.5 ? 1 : -1));
    }

}
