package org.test.models;


import java.sql.Date;
import java.sql.Timestamp;

public class TCabCond {

  private String cc_cb_code;
  private String cc_cd_code;
  private java.sql.Timestamp cc_creadat;
  private java.sql.Timestamp cc_majdate;
  private String cc_majsrc;
  private java.sql.Date cc_abddate;
  private String cc_abdsrc;


  public String getCc_cb_code() {
    return cc_cb_code;
  }

  public void setCc_cb_code(String cc_cb_code) {
    this.cc_cb_code = cc_cb_code;
  }

  public String getCc_cd_code() {
    return cc_cd_code;
  }

  public void setCc_cd_code(String cc_cd_code) {
    this.cc_cd_code = cc_cd_code;
  }

  public Timestamp getCc_creadat() {
    return cc_creadat;
  }

  public void setCc_creadat(Timestamp cc_creadat) {
    this.cc_creadat = cc_creadat;
  }

  public Timestamp getCc_majdate() {
    return cc_majdate;
  }

  public void setCc_majdate(Timestamp cc_majdate) {
    this.cc_majdate = cc_majdate;
  }

  public String getCc_majsrc() {
    return cc_majsrc;
  }

  public void setCc_majsrc(String cc_majsrc) {
    this.cc_majsrc = cc_majsrc;
  }

  public Date getCc_abddate() {
    return cc_abddate;
  }

  public void setCc_abddate(Date cc_abddate) {
    this.cc_abddate = cc_abddate;
  }

  public String getCc_abdsrc() {
    return cc_abdsrc;
  }

  public void setCc_abdsrc(String cc_abdsrc) {
    this.cc_abdsrc = cc_abdsrc;
  }
}
