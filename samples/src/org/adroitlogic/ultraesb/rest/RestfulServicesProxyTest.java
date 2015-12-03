/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.rest;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author asankha
 */
public class RestfulServicesProxyTest extends UTestCase {

    private static final String urlPrefix = "http://localhost:8280/service/rest-proxy";

    public static Test suite() {
        return new UTestSetup(RestfulServicesProxyTest.class, 101);
    }

    public void testCustomerResource() throws Exception {

        // create a new customer
        logger.debug("*** Create a new Customer ***");
        HttpPost httppost = new HttpPost(urlPrefix + "/customers");
        httppost.setEntity(new StringEntity(newCustomerXML, APPLICATION_XML));
        HttpResponse response = client.execute(httppost);
        EntityUtils.consume(response.getEntity()); // discard content

        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatusLine().getStatusCode());
        String location = response.getHeaders("Location")[0].getValue();

        // Get the new customer
        logger.debug("*** GET Created Customer **");
        HttpGet httpget = new HttpGet(location);
        response = client.execute(httpget);
        EntityUtils.consume(response.getEntity());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        // Update the new customer.
        logger.debug("*** PUT - Update the Customer ***");
        HttpPut httpPut = new HttpPut(location);
        httpPut.setEntity(new StringEntity(updateCustomerXML, APPLICATION_XML));
        response = client.execute(httpPut);
        Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getEntity());

        // Patch the new customer.
        logger.debug("*** PATCH - Patch the Customer ***");
        HttpPatch httpPatch = new HttpPatch(location);
        httpPatch.setEntity(new StringEntity(patchCustomerXML, APPLICATION_XML));
        response = client.execute(httpPatch);
        Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getEntity());

        // Show the update
        logger.debug("*** GET - Customer After Update *** from : " + location);
        httpget = new HttpGet(location);
        response = client.execute(httpget);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        String responseContent = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(responseContent.contains("Asankha"));
        Assert.assertTrue(responseContent.contains("Singapore"));

        // Get Header info Only
        logger.debug("*** HEAD for Created Customer *** for URL : " + location);
        HttpHead httphead = new HttpHead(location);
        response = client.execute(httphead);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getEntity());
        for (Header h : response.getAllHeaders()) {
            logger.debug(h.getName() + ": " + h.getValue());
        }

        // Check OPTIONS
        logger.debug("*** Checking server OPTIONS ***");
        HttpOptions httpoptions = new HttpOptions(location);
        response = client.execute(httpoptions);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        String allow = response.getHeaders("Allow")[0].getValue();
        String[] headers = allow.split(", ");
        Set<String> methods = new HashSet<String>(Arrays.asList(headers));
        Assert.assertEquals(methods, expected);
        EntityUtils.consume(response.getEntity());  // discard content

        // Delete the Customer
        logger.debug("*** DELETE Created Customer *** at URL  : " + location);
        HttpDelete httpdelete = new HttpDelete(location);
        response = client.execute(httpdelete);
        Assert.assertEquals(HttpStatus.SC_NO_CONTENT, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getEntity());
    }

    private static final String newCustomerXML = "<customer>"
            + "<first-name>Chamath</first-name>"
            + "<last-name>Perera</last-name>"
            + "<street>12 A 1 Pirivena Road</street>"
            + "<city>Mount Lavinia</city>"
            + "<state>LK</state>"
            + "<zip>10370</zip>"
            + "<country>Sri Lanka</country>"
            + "</customer>";

    private static final String updateCustomerXML = "<customer>"
            + "<first-name>Asankha</first-name>"
            + "<last-name>Perera</last-name>"
            + "<street>12 A 1 Pirivena Road</street>"
            + "<city>Mount Lavinia</city>"
            + "<state>LK</state>"
            + "<zip>10370</zip>"
            + "<country>Sri Lanka</country>"
            + "</customer>";

    private static final String patchCustomerXML = "<customer>"
            + "<country>Singapore</country>"
            + "</customer>";

    private static Set<String> expected = new HashSet<String>();

    static {
        expected.add("DELETE");
        expected.add("OPTIONS");
        expected.add("HEAD");
        expected.add("GET");
        expected.add("PUT");
        expected.add("PATCH");
    }

    private static class HttpPatch extends HttpPost {

        public HttpPatch(String s) {
            super(s);
        }

        public String getMethod()
        {
            return "PATCH";
        }
    }
}

