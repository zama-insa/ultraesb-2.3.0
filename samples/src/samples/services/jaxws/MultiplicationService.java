/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author asankha
 */
@WebService()
public class MultiplicationService {

    @WebMethod()
    public Result multiply(int value1, int value2) {
        return new Result(value1, value2);
    }
}
