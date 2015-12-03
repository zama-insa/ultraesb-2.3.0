/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.multiplicationstresstest;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService()
public class ArrayMultiplication {

    @WebMethod()
    public long[] arrayMultiplication(int[] array1, int[] array2) {
        long[] result = new long[array1.length];

        for (int i = 0; i < array1.length; i++) {
            result[i] = array1[i] * array2[i];
        }
        return result;
    }
}
