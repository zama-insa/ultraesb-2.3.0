package org.adroitlogic.ultraesb.xml;

import junit.framework.Assert;
import junit.framework.Test;
import org.adroitlogic.ultraesb.UTestCase;
import org.adroitlogic.ultraesb.UTestSetup;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;

public class XMLSplitAggregateTest extends UTestCase {

    private static final String scatterGatherURL = "http://localhost:8280/service/xml-scatter-gather";

    public static Test suite() {
        return new UTestSetup(XMLSplitAggregateTest.class, 223);
    }

    public void testScatterGather() throws Exception {

        HttpPost httpPost = new HttpPost(scatterGatherURL);
        httpPost.setEntity(new StringEntity(FileUtils.readFileToString(
            new File("samples/resources/requests/multi-stockquote.xml")), APPLICATION_XML));
        HttpResponse response = client.execute(httpPost);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        final String res = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(res.contains("ADRT"));
        Assert.assertTrue(res.contains("IBM"));
        Assert.assertTrue(res.contains("SUN"));
        Assert.assertTrue(res.contains("MSFT"));
        Assert.assertTrue(res.contains("WMT"));
        Assert.assertTrue(res.contains("SGX"));
        Assert.assertTrue(res.contains("XRX"));
        Assert.assertTrue(res.contains("getQuoteResponse"));
    }
}
