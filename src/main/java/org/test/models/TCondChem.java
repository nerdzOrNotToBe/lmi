package org.test.models;


import java.sql.Date;
import java.sql.Timestamp;

public class TCondChem {

	private String dm_cd_code;
	private String dm_cm_code;
	private java.sql.Timestamp dm_creadat;
	private java.sql.Timestamp dm_majdate;
	private String dm_majsrc;
	private java.sql.Date dm_abddate;
	private String dm_abdsrc;

	public String getDm_cd_code() {
		return dm_cd_code;
	}

	public void setDm_cd_code(String dm_cd_code) {
		this.dm_cd_code = dm_cd_code;
	}

	public String getDm_cm_code() {
		return dm_cm_code;
	}

	public void setDm_cm_code(String dm_cm_code) {
		this.dm_cm_code = dm_cm_code;
	}

	public Timestamp getDm_creadat() {
		return dm_creadat;
	}

	public void setDm_creadat(Timestamp dm_creadat) {
		this.dm_creadat = dm_creadat;
	}

	public Timestamp getDm_majdate() {
		return dm_majdate;
	}

	public void setDm_majdate(Timestamp dm_majdate) {
		this.dm_majdate = dm_majdate;
	}

	public String getDm_majsrc() {
		return dm_majsrc;
	}

	public void setDm_majsrc(String dm_majsrc) {
		this.dm_majsrc = dm_majsrc;
	}

	public Date getDm_abddate() {
		return dm_abddate;
	}

	public void setDm_abddate(Date dm_abddate) {
		this.dm_abddate = dm_abddate;
	}

	public String getDm_abdsrc() {
		return dm_abdsrc;
	}

	public void setDm_abdsrc(String dm_abdsrc) {
		this.dm_abdsrc = dm_abdsrc;
	}
}
