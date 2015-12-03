/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.cxf.common;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This interface describes a JAX-RS root resource. All the JAXRS annotations (except those overridden) will
 * be inherited by classes implementing it.
 */
@Path("/customerservice/")
public interface CustomerService {

    @GET
    @Path("/customers/{id}/")
    Customer getCustomer(@PathParam("id") String id);

    @PUT
    @Path("/customers/")
    Response updateCustomer(Customer customer);

    @POST
    @Path("/customers/")
    Response addCustomer(Customer customer);

    @DELETE
    @Path("/customers/{id}/")
    Response deleteCustomer(@PathParam("id") String id);

}
