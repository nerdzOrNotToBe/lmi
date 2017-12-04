package org.test.models;


import java.sql.Date;
import java.sql.Timestamp;

public class TCableline {

  private String cl_code;
  private String cl_cb_code;
  private String cl_long;
  private String cl_comment;
  private String cl_dtclass;
  private String cl_geolqlt;
  private String cl_geolmod;
  private String cl_geolsrc;
  private java.sql.Timestamp cl_creadat;
  private java.sql.Timestamp cl_majdate;
  private String cl_majsrc;
  private java.sql.Date cl_abddate;
  private String cl_abdsrc;
  private String geom;

  public String getCl_code() {
    return cl_code;
  }

  public void setCl_code(String cl_code) {
    this.cl_code = cl_code;
  }

  public String getCl_cb_code() {
    return cl_cb_code;
  }

  public void setCl_cb_code(String cl_cb_code) {
    this.cl_cb_code = cl_cb_code;
  }

  public String getCl_long() {
    return cl_long;
  }

  public void setCl_long(String cl_long) {
    this.cl_long = cl_long;
  }

  public String getCl_comment() {
    return cl_comment;
  }

  public void setCl_comment(String cl_comment) {
    this.cl_comment = cl_comment;
  }

  public String getCl_dtclass() {
    return cl_dtclass;
  }

  public void setCl_dtclass(String cl_dtclass) {
    this.cl_dtclass = cl_dtclass;
  }

  public String getCl_geolqlt() {
    return cl_geolqlt;
  }

  public void setCl_geolqlt(String cl_geolqlt) {
    this.cl_geolqlt = cl_geolqlt;
  }

  public String getCl_geolmod() {
    return cl_geolmod;
  }

  public void setCl_geolmod(String cl_geolmod) {
    this.cl_geolmod = cl_geolmod;
  }

  public String getCl_geolsrc() {
    return cl_geolsrc;
  }

  public void setCl_geolsrc(String cl_geolsrc) {
    this.cl_geolsrc = cl_geolsrc;
  }

  public Timestamp getCl_creadat() {
    return cl_creadat;
  }

  public void setCl_creadat(Timestamp cl_creadat) {
    this.cl_creadat = cl_creadat;
  }

  public Timestamp getCl_majdate() {
    return cl_majdate;
  }

  public void setCl_majdate(Timestamp cl_majdate) {
    this.cl_majdate = cl_majdate;
  }

  public String getCl_majsrc() {
    return cl_majsrc;
  }

  public void setCl_majsrc(String cl_majsrc) {
    this.cl_majsrc = cl_majsrc;
  }

  public Date getCl_abddate() {
    return cl_abddate;
  }

  public void setCl_abddate(Date cl_abddate) {
    this.cl_abddate = cl_abddate;
  }

  public String getCl_abdsrc() {
    return cl_abdsrc;
  }

  public void setCl_abdsrc(String cl_abdsrc) {
    this.cl_abdsrc = cl_abdsrc;
  }

  public String getGeom() {
    return geom;
  }

  public void setGeom(String geom) {
    this.geom = geom;
  }
}
