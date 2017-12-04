package org.test.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opengis.feature.simple.SimpleFeature;


public class Noeud {

	private String code;

	@JsonIgnore
	private SimpleFeature feature;

	public Noeud() {
	}

	public Noeud(SimpleFeature feature) {
		this.feature = feature;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public SimpleFeature getFeature() {
		return feature;
	}

	public void setFeature(SimpleFeature feature) {
		this.feature = feature;
	}

}
