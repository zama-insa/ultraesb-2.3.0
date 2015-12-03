/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.jaxws;

/**
 * @author asankha
 */
public class PatientInformation {

    private String patientId;
    private String alternateId;
    private String familyName;
    private String givenName;
    private String dateOfBirth;
    private String sex;
    private String address;
    private String maritalStatus;
    private String nkFamilyName;
    private String nkGivenName;
    private String nkRelationship;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAlternateId() {
        return alternateId;
    }

    public void setAlternateId(String alternateId) {
        this.alternateId = alternateId;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getNkFamilyName() {
        return nkFamilyName;
    }

    public void setNkFamilyName(String nkFamilyName) {
        this.nkFamilyName = nkFamilyName;
    }

    public String getNkGivenName() {
        return nkGivenName;
    }

    public void setNkGivenName(String nkGivenName) {
        this.nkGivenName = nkGivenName;
    }

    public String getNkRelationship() {
        return nkRelationship;
    }

    public void setNkRelationship(String nkRelationship) {
        this.nkRelationship = nkRelationship;
    }
}
