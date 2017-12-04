package org.test.models;


import com.vividsolutions.jts.geom.Geometry;

import java.sql.Date;
import java.sql.Timestamp;

public class TNoeud {

  private String nd_code;
  private String nd_codeext;
  private String nd_nom;
  private String nd_coderat;
  private String nd_r1_code;
  private String nd_r2_code;
  private String nd_r3_code;
  private String nd_r4_code;
  private String nd_voie;
  private String nd_type;
  private String nd_type_ep;
  private String nd_comment;
  private String nd_dtclass;
  private String nd_geolqlt;
  private String nd_geolmod;
  private String nd_geolsrc;
  private java.sql.Timestamp nd_creadat;
  private java.sql.Timestamp nd_majdate;
  private String nd_majsrc;
  private java.sql.Date nd_abddate;
  private String nd_abdsrc;
  private String geom;
  private String fx_lmicode;
  private String s_nominale;

  // just for parsing not in db
  private boolean pointBranchement = false;


  public String getNd_code() {
    return nd_code;
  }

  public void setNd_code(String nd_code) {
    this.nd_code = nd_code;
  }

  public String getNd_codeext() {
    return nd_codeext;
  }

  public void setNd_codeext(String nd_codeext) {
    this.nd_codeext = nd_codeext;
  }

  public String getNd_nom() {
    return nd_nom;
  }

  public void setNd_nom(String nd_nom) {
    this.nd_nom = nd_nom;
  }

  public String getNd_coderat() {
    return nd_coderat;
  }

  public void setNd_coderat(String nd_coderat) {
    this.nd_coderat = nd_coderat;
  }

  public String getNd_r1_code() {
    return nd_r1_code;
  }

  public void setNd_r1_code(String nd_r1_code) {
    this.nd_r1_code = nd_r1_code;
  }

  public String getNd_r2_code() {
    return nd_r2_code;
  }

  public void setNd_r2_code(String nd_r2_code) {
    this.nd_r2_code = nd_r2_code;
  }

  public String getNd_r3_code() {
    return nd_r3_code;
  }

  public void setNd_r3_code(String nd_r3_code) {
    this.nd_r3_code = nd_r3_code;
  }

  public String getNd_r4_code() {
    return nd_r4_code;
  }

  public void setNd_r4_code(String nd_r4_code) {
    this.nd_r4_code = nd_r4_code;
  }

  public String getNd_voie() {
    return nd_voie;
  }

  public void setNd_voie(String nd_voie) {
    this.nd_voie = nd_voie;
  }

  public String getNd_type() {
    return nd_type;
  }

  public void setNd_type(String nd_type) {
    this.nd_type = nd_type;
  }

  public String getNd_type_ep() {
    return nd_type_ep;
  }

  public void setNd_type_ep(String nd_type_ep) {
    this.nd_type_ep = nd_type_ep;
  }

  public String getNd_comment() {
    return nd_comment;
  }

  public void setNd_comment(String nd_comment) {
    this.nd_comment = nd_comment;
  }

  public String getNd_dtclass() {
    return nd_dtclass;
  }

  public void setNd_dtclass(String nd_dtclass) {
    this.nd_dtclass = nd_dtclass;
  }

  public String getNd_geolqlt() {
    return nd_geolqlt;
  }

  public void setNd_geolqlt(String nd_geolqlt) {
    this.nd_geolqlt = nd_geolqlt;
  }

  public String getNd_geolmod() {
    return nd_geolmod;
  }

  public void setNd_geolmod(String nd_geolmod) {
    this.nd_geolmod = nd_geolmod;
  }

  public String getNd_geolsrc() {
    return nd_geolsrc;
  }

  public void setNd_geolsrc(String nd_geolsrc) {
    this.nd_geolsrc = nd_geolsrc;
  }

  public Timestamp getNd_creadat() {
    return nd_creadat;
  }

  public void setNd_creadat(Timestamp nd_creadat) {
    this.nd_creadat = nd_creadat;
  }

  public Timestamp getNd_majdate() {
    return nd_majdate;
  }

  public void setNd_majdate(Timestamp nd_majdate) {
    this.nd_majdate = nd_majdate;
  }

  public String getNd_majsrc() {
    return nd_majsrc;
  }

  public void setNd_majsrc(String nd_majsrc) {
    this.nd_majsrc = nd_majsrc;
  }

  public Date getNd_abddate() {
    return nd_abddate;
  }

  public void setNd_abddate(Date nd_abddate) {
    this.nd_abddate = nd_abddate;
  }

  public String getNd_abdsrc() {
    return nd_abdsrc;
  }

  public void setNd_abdsrc(String nd_abdsrc) {
    this.nd_abdsrc = nd_abdsrc;
  }

  public String getGeom() {
    return geom;
  }

  public void setGeom(String geom) {
    this.geom = geom;
  }

  public String getFx_lmicode() {
    return fx_lmicode;
  }

  public void setFx_lmicode(String fx_lmicode) {
    this.fx_lmicode = fx_lmicode;
  }

  public String getS_nominale() {
    return s_nominale;
  }

  public void setS_nominale(String s_nominale) {
    this.s_nominale = s_nominale;
  }

  public boolean isPointBranchement() {
    return pointBranchement;
  }

  public void setPointBranchement(boolean pointBranchement) {
    this.pointBranchement = pointBranchement;
  }
}