/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.cxf.client;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import samples.services.cxf.common.*;

public final class Client {

    private static final String BASE_SERVICE_URL = "http://localhost:9000";

    public static void main(String args[]) throws Exception {
        CustomerService proxy = JAXRSClientFactory.create(BASE_SERVICE_URL, CustomerService.class);
        System.out.println(proxy.getCustomer("123").getName());

        Customer customer = new Customer();
        customer.setId(234);
        customer.setName("Asankha");
        proxy.addCustomer(customer);

        System.out.println(proxy.getCustomer("124").getName());
    }
}
