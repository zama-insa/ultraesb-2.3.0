/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.hessian;

import com.caucho.hessian.server.HessianServlet;
import samples.services.soap.GQResponse;
import samples.services.soap.GQ;

import java.util.Date;

/**
 * @author asankha
 */
public class HessianStockQuoteService extends HessianServlet implements StockQuoteService {
    
    public GQResponse getQuote(GQ request) {
        System.out.println(new Date() + " " + this.getClass().getName() +
            " :: Generating quote for : " + request.getSymbol());
        return new GQResponse(request.getSymbol());
    }
}
