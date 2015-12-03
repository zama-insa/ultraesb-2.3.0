/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.hessiantest;

import com.caucho.hessian.server.HessianServlet;

public class ArrayMultiplicationHessian extends HessianServlet implements MultiplicationService {

    public long[] arrayMultiplication(int value1[], int value2[]) {
        long[] result = new long[value1.length];
        int count = 0;
        for (; count < value1.length; count++) {
            result[count] = value1[count] * value2[count];
        }
        return result;
    }
}