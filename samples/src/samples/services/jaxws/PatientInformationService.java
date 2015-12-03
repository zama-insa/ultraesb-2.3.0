/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author asankha
 */
@WebService()
public class PatientInformationService {

    @WebMethod()
    public PatientInformation getPatientInformation(@WebParam(name = "id") String id) {
        PatientInformation p = new PatientInformation();
        p.setPatientId(id);
        p.setAlternateId("508003");
        p.setFamilyName("Bauer");
        p.setGivenName("Fritz");
        p.setDateOfBirth("19631101");
        p.setSex("M");
        p.setAddress("34B Windsor Ridge, Dulles, VA 68123");
        p.setMaritalStatus("M");
        p.setNkFamilyName("Bauer");
        p.setNkGivenName("Karin");
        p.setNkRelationship("Wife");
        return p;
    }
}
