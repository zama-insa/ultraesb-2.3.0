/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.hessian;

import samples.services.soap.GQResponse;
import samples.services.soap.GQ;

/**
 * @author asankha
 */
public interface StockQuoteService {
    public GQResponse getQuote(GQ request);
}
