/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.multiplicationstresstest;

import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;
import samples.services.StressTestStats;
import samples.services.arraymultiplicationtest.ArrayMultiplicationService;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArrayMultiplicationTest implements Runnable {

    ArrayMultiplicationService service = null;
    samples.services.arraymultiplicationtest.ArrayMultiplication port = null;

    private int arraySize = 1000;
    StressTestStats stats = null;
    private String surl = null;
    private String[] header = new String[2];
    private String headerValue = null;

    public ArrayMultiplicationTest(StressTestStats stats, String surl, int arraySize, String header, String headerValue) {
        super();
        this.stats = stats;
        this.surl = surl;
        this.arraySize = arraySize;
        this.header = getHeaderDetails(header);
        this.headerValue = headerValue;

        if (this.surl != null) {
            try {
                service = new ArrayMultiplicationService(new URL(this.surl),
                        new QName("http://multiplicationstresstest.services.samples/", "ArrayMultiplicationService"));
                port = service.getArrayMultiplicationPort();
                BindingProvider bp = (BindingProvider) port;
                bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.surl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                service = new ArrayMultiplicationService(new URL("file:samples/resources/ArrayMultiplication.wsdl"),
                        new QName("http://stressTest.services.samples/", "ArrayMultiplicationService"));
                port = service.getArrayMultiplicationPort();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        WSBindingProvider wsbp = (WSBindingProvider) port;
        if (this.header[0] == null) {
            wsbp.setOutboundHeaders(Headers.create(new QName(this.header[1]), this.headerValue));
        } else {
            wsbp.setOutboundHeaders(Headers.create(new QName(this.header[0], this.header[1]), this.headerValue));
        }
    }

    public void run() {

        Random randomGenerator = new Random(System.currentTimeMillis());

        List<Integer> array1 = new ArrayList<Integer>();
        List<Integer> array2 = new ArrayList<Integer>();

        for (int index = 0; index < arraySize; index++) {
            array1.add(randomGenerator.nextInt(1000));
            array2.add(randomGenerator.nextInt(1000));
        }

        List<Long> expected = checkResult(array1, array2);

        List<Long> result = port.arrayMultiplication(array1, array2);

        if (compare(expected, result)) {
            stats.incTotalCount(true);
        } else {
            stats.incTotalCount(false);
        }
    }

    public List<Long> checkResult(List<Integer> array1, List<Integer> array2) {
        List<Long> result = new ArrayList<Long>();
        for (int i = 0; i < arraySize; i++) {
            result.add((long) array1.get(i) * (long) array2.get(i));
        }
        return result;
    }

    public boolean compare(List<Long> expected, List<Long> result) {
        boolean equals = false;
        for (int i = 0; i < arraySize; i++) {
            if (expected.get(i).equals(result.get(i))) {
                equals = true;
            }
        }
        return equals;
    }

    public String[] getHeaderDetails(String header) {
        String[] headerValues = new String[2];
        int start = header.indexOf('{');
        int end = header.indexOf('}');

        if (start == -1 && end == -1) {
            headerValues[0] = null;
            headerValues[1] = header;
        } else {
            headerValues[0] = header.substring(start + 1, end);
            headerValues[1] = header.substring(end + 1);
        }
        return headerValues;
    }
}