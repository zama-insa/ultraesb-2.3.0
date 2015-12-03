/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.soap;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import samples.services.soap.SimpleStockQuoteService;

/**
 * @author asankha
 */
public class JaxWSTest extends UTestCase {

    public static Test suite() {
        return new UTestSetup(JaxWSTest.class, 202);
    }

    public void testJaxWSCall() throws Exception {

        SimpleStockQuoteService port = new SimpleStockQuoteService();

        System.out.println("Last sale was at : " + port.getSimpleQuote("ADRT").getLast());
        System.out.println("Last sale was at : " + port.getSimpleQuote("ADRT").getLast());
        System.out.println("Last sale was at : " + port.getSimpleQuote("ADRT").getLast());
        Assert.assertEquals(port.getSimpleQuote("ADRT").getName(), "ADRT Company");
    }
}
