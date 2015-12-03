/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.soap;

public class GMA {
    String[] symbols;

    public GMA() {
    }

    public GMA(String[] symbols) {
        this.symbols = symbols;
    }

    public String[] getSymbols() {
        return symbols;
    }

    public void setSymbols(String[] symbols) {
        this.symbols = symbols;
    }
}
