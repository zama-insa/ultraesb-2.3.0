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

package sample;

import org.adroitlogic.ultraesb.api.*;

import java.lang.String;

/**
 * @author asankha
 */
public class SimpleJavaMediation implements JavaClassSequence {

    private long startTime;
    private int count;

    public void execute(Message msg, Mediation mediation) throws Exception {
        System.out.println("Message target :  " + msg.getDestinationURL());
        if (mediation.getXMLSupport().filter(msg, "//soap:getQuote/request/symbol", "ADRT", new String[] {"soap", "http://soap.services.samples/"})) {
            mediation.sendToEndpoint(msg, "stockquote");
        } else {
            mediation.sendToEndpoint(msg, "stockquote-err");
        }
        count++;
    }

    public void init(Configuration config) {
        startTime = System.currentTimeMillis();
    }

    public void destroy() {
        System.out.println("SimpleJavaMediation sequence, Execution time : "
            + (System.currentTimeMillis() - startTime) +
            "ms. Processed : " + count + " messages");
    }
}
