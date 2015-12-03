/*
 * Copyright (c) 2010-2011 AdroitLogic Private Ltd. All Rights Reserved.
 */

package org.adroitlogic.ultraesb.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="msg", namespace="http://test.com/msg")
@XmlAccessorType(XmlAccessType.FIELD)
public class Msg {

    String id;
    String subject;
    String date;
    String type;
    String code;

    public Msg() {
        super();}

    public Msg(String id, String subject, String date, String type,
        String code) {
        super();
        this.id = id;
        this.subject = subject;
        this.date = date;
        this.type = type;
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}