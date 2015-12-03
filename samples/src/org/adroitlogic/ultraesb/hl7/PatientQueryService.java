/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.hl7;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v22.message.ADR_A19;
import ca.uhn.hl7v2.model.v22.group.*;
import ca.uhn.hl7v2.model.v22.message.QRY_A19;
import ca.uhn.hl7v2.model.v22.segment.*;
import ca.uhn.hl7v2.parser.DefaultModelClassFactory;
import ca.uhn.hl7v2.parser.GenericParser;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;


/**
 * @author asankha
 */
public class PatientQueryService {

    public static org.adroitlogic.ultraesb.api.Message getResponse(Message hapiReq) throws Exception {

        ADR_A19 adr = new ADR_A19();

        MSH mshSegment = adr.getMSH();
        mshSegment.getFieldSeparator().setValue("|");
        mshSegment.getEncodingCharacters().setValue("^~\\&");
        mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue("200901011539");
        mshSegment.getSendingApplication().setValue("UltraESB");
        //mshSegment.getSequenceNumber().setValue("123");
        mshSegment.getMessageType().getMessageType().setValue("ADR");
        mshSegment.getMessageType().getTriggerEvent().setValue("A19");
        //mshSegment.getMessageType().getMessageStructure().setValue("ADT A19");

        adr.getMSA().getAcknowledgementCode().setValue("AA");

        ADR_A19_QUERY_RESPONSE qr = adr.getQUERY_RESPONSE();

        PID pid = qr.getPID();
        pid.getPatientName().getFamilyName().setValue("Robert");
        pid.getPatientName().getGivenName().setValue("Johnson");
//        pid.getPatientAlias(0). getPatientIdentifierList(0).getID().setValue("X123456");

        DG1 dg1 = qr.getDG1();
//        dg1.getSetIDDG1().setValue("3");
        dg1.getDg13_DiagnosisCode().setValue("Stomach");
        dg1.getDiagnosisDescription().setValue("Appendicitis");

        PV1 pv1 = qr.getPV1();
        pv1.getPatientClass().setValue("S");
        pv1.getAssignedPatientLocation().getBed().setValue("BED 134");
        pv1.getAssignedPatientLocation().getFacilityID().setValue("FACILITY MRI");
        pv1.getAssignedPatientLocation().getBed().setValue("4");
        pv1.getAdmissionType().setValue("R");
        pv1.insertPv19_ConsultingDoctor(0).getIDNumber().setValue("20 56 344");
        pv1.insertPv19_ConsultingDoctor(0).getFamilyName().setValue("Wedarala");
        pv1.insertPv19_ConsultingDoctor(0).getGivenName().setValue("Punchi");
        pv1.insertPv19_ConsultingDoctor(0).getIDNumber().setValue("D8564");

        Parser parser = new PipeParser();
        String encodedMessage = parser.encode(adr);
        System.out.println("Current response : " + encodedMessage);
        return null;
    }

    public static void main(String[] args) throws Exception {

        Parser p = new GenericParser();
        Message hapiMsg = p.parse("MSH|^~\\&|KIS||CommServer||200811111017||QRY^A19||P|2.4|\r" +
            "QRD|200811111016|R|I|Q1004|||1^RD|10000437363|DEM|||");

        getResponse(hapiMsg);
    }
}
