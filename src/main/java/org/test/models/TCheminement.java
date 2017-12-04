package org.test.models;



import com.vividsolutions.jts.geom.Geometry;

import java.sql.Date;
import java.sql.Timestamp;

public class TCheminement {

  private String cm_code;
  private String cm_codeext;
  private String cm_ndcode1;
  private String cm_ndcode2;
  private String cm_cm1;
  private String cm_cm2;
  private String cm_r1_code;
  private String cm_r2_code;
  private String cm_r3_code;
  private String cm_r4_code;
  private String cm_voie;
  private String cm_gest_do;
  private String cm_prop_do;
  private String cm_statut;
  private String cm_etat;
  private java.sql.Date cm_datcons;
  private java.sql.Date cm_datemes;
  private String cm_avct;
  private String cm_typelog;
  private String cm_typ_imp;
  private String cm_nature;
  private String cm_compo;
  private long cm_cddispo;
  private long cm_fo_util;
  private String cm_mod_pos;
  private String cm_passage;
  private String cm_revet;
  private String cm_remblai;
  private String cm_charge;
  private String cm_larg;
  private String cm_fildtec;
  private String cm_mut_org;
  private String cm_long;
  private String cm_lgreel;
  private String cm_comment;
  private String cm_dtclass;
  private String cm_geolqlt;
  private String cm_geolmod;
  private String cm_geolsrc;
  private java.sql.Timestamp cm_creadat;
  private java.sql.Timestamp cm_majdate;
  private String cm_majsrc;
  private java.sql.Date cm_abddate;
  private String cm_abdsrc;
  private String geom;
  private String fx_lmicode;
  private String s_nominal;
  private String infra_type;
  private String infra_lib;

  public String getCm_code() {
    return cm_code;
  }

  public void setCm_code(String cm_code) {
    this.cm_code = cm_code;
  }

  public String getCm_codeext() {
    return cm_codeext;
  }

  public void setCm_codeext(String cm_codeext) {
    this.cm_codeext = cm_codeext;
  }

  public String getCm_ndcode1() {
    return cm_ndcode1;
  }

  public void setCm_ndcode1(String cm_ndcode1) {
    this.cm_ndcode1 = cm_ndcode1;
  }

  public String getCm_ndcode2() {
    return cm_ndcode2;
  }

  public void setCm_ndcode2(String cm_ndcode2) {
    this.cm_ndcode2 = cm_ndcode2;
  }

  public String getCm_cm1() {
    return cm_cm1;
  }

  public void setCm_cm1(String cm_cm1) {
    this.cm_cm1 = cm_cm1;
  }

  public String getCm_cm2() {
    return cm_cm2;
  }

  public void setCm_cm2(String cm_cm2) {
    this.cm_cm2 = cm_cm2;
  }

  public String getCm_r1_code() {
    return cm_r1_code;
  }

  public void setCm_r1_code(String cm_r1_code) {
    this.cm_r1_code = cm_r1_code;
  }

  public String getCm_r2_code() {
    return cm_r2_code;
  }

  public void setCm_r2_code(String cm_r2_code) {
    this.cm_r2_code = cm_r2_code;
  }

  public String getCm_r3_code() {
    return cm_r3_code;
  }

  public void setCm_r3_code(String cm_r3_code) {
    this.cm_r3_code = cm_r3_code;
  }

  public String getCm_r4_code() {
    return cm_r4_code;
  }

  public void setCm_r4_code(String cm_r4_code) {
    this.cm_r4_code = cm_r4_code;
  }

  public String getCm_voie() {
    return cm_voie;
  }

  public void setCm_voie(String cm_voie) {
    this.cm_voie = cm_voie;
  }

  public String getCm_gest_do() {
    return cm_gest_do;
  }

  public void setCm_gest_do(String cm_gest_do) {
    this.cm_gest_do = cm_gest_do;
  }

  public String getCm_prop_do() {
    return cm_prop_do;
  }

  public void setCm_prop_do(String cm_prop_do) {
    this.cm_prop_do = cm_prop_do;
  }

  public String getCm_statut() {
    return cm_statut;
  }

  public void setCm_statut(String cm_statut) {
    this.cm_statut = cm_statut;
  }

  public String getCm_etat() {
    return cm_etat;
  }

  public void setCm_etat(String cm_etat) {
    this.cm_etat = cm_etat;
  }

  public Date getCm_datcons() {
    return cm_datcons;
  }

  public void setCm_datcons(Date cm_datcons) {
    this.cm_datcons = cm_datcons;
  }

  public Date getCm_datemes() {
    return cm_datemes;
  }

  public void setCm_datemes(Date cm_datemes) {
    this.cm_datemes = cm_datemes;
  }

  public String getCm_avct() {
    return cm_avct;
  }

  public void setCm_avct(String cm_avct) {
    this.cm_avct = cm_avct;
  }

  public String getCm_typelog() {
    return cm_typelog;
  }

  public void setCm_typelog(String cm_typelog) {
    this.cm_typelog = cm_typelog;
  }

  public String getCm_typ_imp() {
    return cm_typ_imp;
  }

  public void setCm_typ_imp(String cm_typ_imp) {
    this.cm_typ_imp = cm_typ_imp;
  }

  public String getCm_nature() {
    return cm_nature;
  }

  public void setCm_nature(String cm_nature) {
    this.cm_nature = cm_nature;
  }

  public String getCm_compo() {
    return cm_compo;
  }

  public void setCm_compo(String cm_compo) {
    this.cm_compo = cm_compo;
  }

  public long getCm_cddispo() {
    return cm_cddispo;
  }

  public void setCm_cddispo(long cm_cddispo) {
    this.cm_cddispo = cm_cddispo;
  }

  public long getCm_fo_util() {
    return cm_fo_util;
  }

  public void setCm_fo_util(long cm_fo_util) {
    this.cm_fo_util = cm_fo_util;
  }

  public String getCm_mod_pos() {
    return cm_mod_pos;
  }

  public void setCm_mod_pos(String cm_mod_pos) {
    this.cm_mod_pos = cm_mod_pos;
  }

  public String getCm_passage() {
    return cm_passage;
  }

  public void setCm_passage(String cm_passage) {
    this.cm_passage = cm_passage;
  }

  public String getCm_revet() {
    return cm_revet;
  }

  public void setCm_revet(String cm_revet) {
    this.cm_revet = cm_revet;
  }

  public String getCm_remblai() {
    return cm_remblai;
  }

  public void setCm_remblai(String cm_remblai) {
    this.cm_remblai = cm_remblai;
  }

  public String getCm_charge() {
    return cm_charge;
  }

  public void setCm_charge(String cm_charge) {
    this.cm_charge = cm_charge;
  }

  public String getCm_larg() {
    return cm_larg;
  }

  public void setCm_larg(String cm_larg) {
    this.cm_larg = cm_larg;
  }

  public String getCm_fildtec() {
    return cm_fildtec;
  }

  public void setCm_fildtec(String cm_fildtec) {
    this.cm_fildtec = cm_fildtec;
  }

  public String getCm_mut_org() {
    return cm_mut_org;
  }

  public void setCm_mut_org(String cm_mut_org) {
    this.cm_mut_org = cm_mut_org;
  }

  public String getCm_long() {
    return cm_long;
  }

  public void setCm_long(String cm_long) {
    this.cm_long = cm_long;
  }

  public String getCm_lgreel() {
    return cm_lgreel;
  }

  public void setCm_lgreel(String cm_lgreel) {
    this.cm_lgreel = cm_lgreel;
  }

  public String getCm_comment() {
    return cm_comment;
  }

  public void setCm_comment(String cm_comment) {
    this.cm_comment = cm_comment;
  }

  public String getCm_dtclass() {
    return cm_dtclass;
  }

  public void setCm_dtclass(String cm_dtclass) {
    this.cm_dtclass = cm_dtclass;
  }

  public String getCm_geolqlt() {
    return cm_geolqlt;
  }

  public void setCm_geolqlt(String cm_geolqlt) {
    this.cm_geolqlt = cm_geolqlt;
  }

  public String getCm_geolmod() {
    return cm_geolmod;
  }

  public void setCm_geolmod(String cm_geolmod) {
    this.cm_geolmod = cm_geolmod;
  }

  public String getCm_geolsrc() {
    return cm_geolsrc;
  }

  public void setCm_geolsrc(String cm_geolsrc) {
    this.cm_geolsrc = cm_geolsrc;
  }

  public Timestamp getCm_creadat() {
    return cm_creadat;
  }

  public void setCm_creadat(Timestamp cm_creadat) {
    this.cm_creadat = cm_creadat;
  }

  public Timestamp getCm_majdate() {
    return cm_majdate;
  }

  public void setCm_majdate(Timestamp cm_majdate) {
    this.cm_majdate = cm_majdate;
  }

  public String getCm_majsrc() {
    return cm_majsrc;
  }

  public void setCm_majsrc(String cm_majsrc) {
    this.cm_majsrc = cm_majsrc;
  }

  public Date getCm_abddate() {
    return cm_abddate;
  }

  public void setCm_abddate(Date cm_abddate) {
    this.cm_abddate = cm_abddate;
  }

  public String getCm_abdsrc() {
    return cm_abdsrc;
  }

  public void setCm_abdsrc(String cm_abdsrc) {
    this.cm_abdsrc = cm_abdsrc;
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

  public String getS_nominal() {
    return s_nominal;
  }

  public void setS_nominal(String s_nominal) {
    this.s_nominal = s_nominal;
  }

  public String getInfra_type() {
    return infra_type;
  }

  public void setInfra_type(String infra_type) {
    this.infra_type = infra_type;
  }

  public String getInfra_lib() {
    return infra_lib;
  }

  public void setInfra_lib(String infra_lib) {
    this.infra_lib = infra_lib;
  }
}
