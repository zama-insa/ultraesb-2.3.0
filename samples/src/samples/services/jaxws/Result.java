/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.jaxws;

/**
 * @author asankha
 */
public class Result {
    int value1;
    int value2;
    long result;

    public Result() {
    }

    Result(int value1, int value2) {
        this.value1 = value1;
        this.value2 = value2;
        this.result = value1 * value2;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }
}
