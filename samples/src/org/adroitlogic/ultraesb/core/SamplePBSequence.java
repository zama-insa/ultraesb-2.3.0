/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package org.adroitlogic.ultraesb.core;

import org.adroitlogic.ultraesb.api.Configuration;
import org.adroitlogic.ultraesb.api.JavaClassSequence;
import org.adroitlogic.ultraesb.api.Mediation;
import org.adroitlogic.ultraesb.api.Message;
import samples.protocolbuffer.AddressBookProtos;

/**
 * Sample Java sequence demonstrating a protocol buffer message processing as a protocol buffer Java type
 *
 * @author Ruwan
 * @see 1.7.0
 */
@SuppressWarnings({"UnusedDeclaration"})
public class SamplePBSequence implements JavaClassSequence {

    public void init(Configuration config) {}
    public void destroy() {}

    public void execute(Message msg, Mediation mediation) throws Exception {

        // convert the message to a typed protocol buffer message
        mediation.getProtocolBufferSupport().convertToProtocolBuffer(msg, AddressBookProtos.AddressBook.class);
        // retrieve the typed protocol buffer message
        AddressBookProtos.AddressBook book = mediation.getProtocolBufferSupport().getMessage(
            msg, AddressBookProtos.AddressBook.class);

        // adds a new person to the address book
        AddressBookProtos.Person.Builder person = AddressBookProtos.Person.newBuilder();
        person.setId(789);
        person.setName("Ruwan Linton");
        person.setEmail("ruwan@adroitlogic.com");

        // set the modified message as a builder to the protocol buffer message
        mediation.getProtocolBufferSupport().setMessage(msg, book.toBuilder().addPerson(person.build()));

        // reply back with the modified address book
        mediation.sendResponse(msg, 200);
    }
}
