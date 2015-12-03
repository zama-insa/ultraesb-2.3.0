/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.cxf.server;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

public class TestServer {

    private Server server;

    public void start() throws Exception {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
        sf.setResourceClasses(CustomerServiceImpl.class);
        sf.setResourceProvider(CustomerServiceImpl.class, new SingletonResourceProvider(new CustomerServiceImpl()));
        sf.setAddress("http://localhost:9000/");
        server = sf.create();
        server.start();
    }

    public void stop() {
        server.stop();
    }

    public static void main(String[] args) throws Exception {
        new TestServer().start();
        Thread.sleep(60000);
    }
}