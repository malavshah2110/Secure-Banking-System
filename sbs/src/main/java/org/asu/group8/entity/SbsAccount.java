package org.asu.group8.entity;

import javax.persistence.*;

@Entity
@Table(name = "sbs_account")
public class SbsAccount {

    public SbsAccount() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // information
    private Long accountNumber;
    private String accountType;     // checking / savings / credit
    private Double accountBalance;   // can be modified

    // the card info is stored in this table (sbs_account)
    private String cardNumber;
    private String cvv;
    private Double creditLimit;      // can be modified
    private Double latePayment;      // can be modified
    private Double creditInterest;   // can be modified

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sbs_user_id")
    private SbsUser sbsUser;

    public SbsUser getSbsUser() {
        return sbsUser;
    }

    public void setSbsUser(SbsUser sbsUser) {
        setSbsUser(sbsUser, true);
    }

    void setSbsUser(SbsUser sbsUser, boolean add) {
        this.sbsUser = sbsUser;
        if (sbsUser != null && add) {
            sbsUser.addSbsAccount(this, false);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public Double getLatePayment() {
        return latePayment;
    }

    public void setLatePayment(Double latePayment) {
        this.latePayment = latePayment;
    }

    public Double getCreditInterest() {
        return creditInterest;
    }

    public void setCreditInterest(Double creditInterest) {
        this.creditInterest = creditInterest;
    }
}
