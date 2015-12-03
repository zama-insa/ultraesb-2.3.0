/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.json;

import org.adroitlogic.ultraesb.api.helper.SplitGroupingCriteria;
import org.codehaus.jackson.JsonNode;

/**
 * todo: describe me please
 *
 * @author Ruwan
 * @since 2.1.0
 */
public class SampleSplitGroupCriteria implements SplitGroupingCriteria<JsonNode> {

    @Override
    public String calculate(JsonNode type) {
        return type.get("orderId").getTextValue().substring(20);
    }
}
