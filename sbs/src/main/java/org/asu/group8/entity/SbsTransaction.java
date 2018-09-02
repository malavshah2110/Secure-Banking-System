package org.asu.group8.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sbs_transaction")
public class SbsTransaction {

    public SbsTransaction() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;

    // information
    private String type;
    private Date timestamp;

    private Long fromAccount;
    private Long toAccount;

    private Double amount;
    private Boolean critical;
    private Boolean holdInternal;
    private Boolean holdTransfer;
    private Boolean completed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Long fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Long getToAccount() {
        return toAccount;
    }

    public void setToAccount(Long toAccount) {
        this.toAccount = toAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getCritical() {
        return critical;
    }

    public void setCritical(Boolean critical) {
        this.critical = critical;
    }

    public Boolean getHoldInternal() {
        return holdInternal;
    }

    public void setHoldInternal(Boolean holdInternal) {
        this.holdInternal = holdInternal;
    }

    public Boolean getHoldTransfer() {
        return holdTransfer;
    }

    public void setHoldTransfer(Boolean holdTransfer) {
        this.holdTransfer = holdTransfer;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}