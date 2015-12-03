/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.drools;

import java.io.Serializable;

/**
 * A POJO to manipulate a loan application
 *
 * @author amindri udugala
 */
public class LoanApplication implements Serializable {

    private String firstName;
    private String lastName;
    private String state;
    private String bank;
    private int age;
    private int creditLimit;
    private int previousApplications;
    private boolean loanApproved;
    private int interestRate;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public boolean isLoanApproved() {
        return loanApproved;
    }

    public void setLoanApproved(boolean loanApproved) {
        this.loanApproved = loanApproved;
    }

    public int getPreviousApplications() {
        return previousApplications;
    }

    public void setPreviousApplications(int previousApplications) {
        this.previousApplications = previousApplications;
    }

    public int getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(int interestRate) {
        this.interestRate = interestRate;
    }
}
