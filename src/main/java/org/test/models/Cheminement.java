package org.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opengis.feature.simple.SimpleFeature;

import java.io.Serializable;

public class Cheminement implements Serializable{

	private String code;

	private Noeud noeud1;
	private Noeud noeud2;
	private Cheminement cheminenement1;
	private Cheminement cheminenement2;

	@JsonIgnore
	private SimpleFeature feature;

	public Cheminement(SimpleFeature feature) {
		this.feature = feature;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Noeud getNoeud1() {
		return noeud1;
	}

	public void setNoeud1(Noeud noeud1) {
		this.noeud1 = noeud1;
	}

	public Noeud getNoeud2() {
		return noeud2;
	}

	public void setNoeud2(Noeud noeud2) {
		this.noeud2 = noeud2;
	}

	public SimpleFeature getFeature() {
		return feature;
	}

	public void setFeature(SimpleFeature feature) {
		this.feature = feature;
	}

	public Cheminement getCheminenement1() {
		return cheminenement1;
	}

	public void setCheminenement1(Cheminement cheminenement1) {
		this.cheminenement1 = cheminenement1;
	}

	public Cheminement getCheminenement2() {
		return cheminenement2;
	}

	public void setCheminenement2(Cheminement cheminenement2) {
		this.cheminenement2 = cheminenement2;
	}
}
