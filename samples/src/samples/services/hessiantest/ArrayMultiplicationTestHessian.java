/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.hessiantest;

import com.caucho.hessian.client.HessianProxyFactory;
import samples.services.StressTestStats;

import java.net.MalformedURLException;
import java.util.Random;

public class ArrayMultiplicationTestHessian implements Runnable {

    HessianProxyFactory factory = null;
    MultiplicationService basic = null;

    private int arraySize = 1000;
    StressTestStats stats = null;
    private String hurl = null;
    private String header = null;
    private String headerValue = null;

    public ArrayMultiplicationTestHessian(StressTestStats stats, String hurl, int arraySize, String header, String headerValue) {
        super();
        this.stats = stats;
        this.hurl = hurl;
        this.arraySize = arraySize;
        this.header = header;
        this.headerValue = headerValue;

        if (this.hurl == null) {
            this.hurl = "http://localhost:9000/hessiantest/hessian-stress-test";
        }

        try {
            factory = new HessianProxyFactory();
            basic = (MultiplicationService) factory.create(MultiplicationService.class, this.hurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        Random randomGenerator = new Random(System.currentTimeMillis());

        int[] array1 = new int[arraySize];
        int[] array2 = new int[arraySize];

        for (int index = 0; index < arraySize; index++) {
            array1[index] = randomGenerator.nextInt(1000);
            array2[index] = randomGenerator.nextInt(1000);
        }

        long[] expected = checkResult(array1, array2);

        long[] result = basic.arrayMultiplication(array1, array2);

        if (compare(expected, result)) {
            stats.incTotalCount(true);
        } else {
            stats.incTotalCount(false);
        }
    }

    public long[] checkResult(int[] array1, int[] array2) {
        long[] result = new long[arraySize];
        for (int i = 0; i < arraySize; i++) {
            result[i] = (long) array1[i] * (long) array2[i];
        }
        return result;
    }

    public boolean compare(long[] expected, long[] result) {
        boolean equals = false;
        for (int i = 0; i < arraySize; i++) {
            if (expected[i] == result[i]) {
                equals = true;
            }
        }
        return equals;
    }
}
