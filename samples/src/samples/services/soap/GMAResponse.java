/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.soap;

public class GMAResponse {

    GQResponse[] quotes = null;

    public GMAResponse() {
    }

    public GMAResponse(String[] symbols) {
        quotes = new GQResponse[symbols.length];
        for (int i=0; i<symbols.length; i++) {
            quotes[i] = new GQResponse(symbols[i]);
        }
    }

    public GQResponse[] getQuotes() {
        return quotes;
    }

    public void setQuotes(GQResponse[] quotes) {
        this.quotes = quotes;
    }
}
