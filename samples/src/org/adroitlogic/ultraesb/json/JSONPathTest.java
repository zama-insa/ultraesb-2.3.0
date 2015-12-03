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

package org.adroitlogic.ultraesb.json;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.mediation.JSONSupport;
import org.adroitlogic.ultraesb.core.ConfigurationImpl;
import org.adroitlogic.ultraesb.core.MediationImpl;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This is to test extracting elements from a json payload with json path
 *
 * @author Amila Paranawithana
 */
public class JSONPathTest extends TestCase {

    private static final String JSON_ARRAY_PAYLOAD = "[\n" +
        "    {\n" +
        "        \"a\": 1,\n" +
        "        \"b\": 2,\n" +
        "        \"value\": 40,\n" +
        "        \"string_val\":\"aa\",\n" +
        "        \"nullElement\":null,\n" +
        "        \"booleanElement\":true\n" +
        "    },\n" +
        "    {\n" +
        "        \"a\": 4,\n" +
        "        \"b\": 5,\n" +
        "        \"value\": 35,\n" +
        "        \"string_val\":\"bb\",\n" +
        "        \"booleanElement\":false\n" +
        "    }\n" +
        "]";

    private static String JSON_PAYLOAD = null;

    protected void setUp() throws Exception {
        super.setUp();
        MediationImpl.initialize(new ConfigurationImpl());
        JSON_PAYLOAD = FileUtils.readFileToString(new File("samples/resources/requests/sample.json"));
    }

    public void testJsonPathForJsonPayload() throws Exception {
        final Mediation mediation = MediationImpl.getInstance();
        final JSONSupport jsonSupport = mediation.getJSONSupport();

        // extracting string types
        Assert.assertEquals("55555A", jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[0].string_id"));

        // extracting integer types
        Assert.assertEquals(25, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[0].node_value"));

        // extracting boolean types
        Assert.assertEquals(true, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[0].booleanVal"));
        Assert.assertEquals(false, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[1].booleanVal"));

        // extracting null value
        Assert.assertEquals(null, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[1].valueNull"));

        // extracting lists
        List<Integer> intListId = new ArrayList<Integer>();
        intListId.add(55555);
        intListId.add(66666);
        Assert.assertEquals(intListId, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[*].id"));

        List<String> stringList = new ArrayList<String>();
        stringList.add("55555A");
        stringList.add("66666B");
        Assert.assertEquals(stringList, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[*].string_id"));

        // conditional extraction
        List<Integer> intListCondition = new ArrayList<Integer>();
        intListCondition.add(55555);
        Assert.assertEquals(intListCondition, jsonSupport.extractUsingJSONPath(JSON_PAYLOAD, "$.OutterNode.InnerNode1[?(@.node_value > 16)].id"));
    }

    public void testJsonPathForArray() throws Exception {
        final Mediation mediation = MediationImpl.getInstance();
        final JSONSupport jsonSupport = mediation.getJSONSupport();

        // extracting string types
        Assert.assertEquals("aa", jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[0].string_val"));

        // extracting integer types
        Assert.assertEquals(4, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[1].a"));

        // extracting boolean types
        Assert.assertEquals(true, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[0].booleanElement"));
        Assert.assertEquals(false, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[1].booleanElement"));

        // extracting null value
        Assert.assertEquals(null, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[0].nullElement"));

        // extracting lists
        List<Integer> intList = new ArrayList<Integer>();
        intList.add(1);
        intList.add(4);
        Assert.assertEquals(intList, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[*].a"));

        List<String> stringList = new ArrayList<String>();
        stringList.add("aa");
        stringList.add("bb");
        Assert.assertEquals(stringList, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[*].string_val"));

        // conditional extraction
        List<Integer> intListCondition = new ArrayList<Integer>();
        intListCondition.add(1);
        Assert.assertEquals(intListCondition, jsonSupport.extractUsingJSONPath(JSON_ARRAY_PAYLOAD, "$.[?(@.value > 35)].a"));
    }
}
