package org.asu.group8.entity;

import com.sun.javafx.css.CalculatedValue;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "sbs_device")
public class SbsDevice {

    public SbsDevice() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String code;        // cookie code (100 digits)
    private Date expiration;    // expiration data

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sbs_user_id")
    private SbsUser sbsUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public SbsUser getSbsUser() {
        return sbsUser;
    }

    public void setSbsUser(SbsUser sbsUser) {
        setSbsUser(sbsUser, true);
    }

    void setSbsUser(SbsUser sbsUser, boolean add) {
        this.sbsUser = sbsUser;
        if (sbsUser != null && add) {
            sbsUser.addSbsDevice(this, false);
        }
    }

}
