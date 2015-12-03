/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.drools;

import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * A test case to check the drools support in UltraESB
 *
 * @author amindri udugala
 */
public class LoanApplicationTest extends UTestCase {

    String message = "{\"firstName\":\"Jhon\",\n" +
            "\"lastName\":\"Smith\",\n" +
            "\"state\":\"NY\",\n" +
            "\"bank\":\"City\",\n" +
            "\"creditLimit\": 250000,\n" +
            "\"previousApplications\":1,\n" +
            "\"age\":44}";

    public static Test suite() {
        return new UTestSetup(LoanApplicationTest.class, 955);
    }

    public void testCheckLoanApprovalDetails() throws Exception {

        HttpPost httppost = new HttpPost("http://localhost:8280/service/loan-app");
        httppost.setEntity(new StringEntity(message, TEXT_XML));
        HttpEntity entity = client.execute(httppost).getEntity();

        assertEquals("Your loan application is approved. The interest rate is, 3%", EntityUtils.toString(entity));
    }
}
