package org.test;

import com.vividsolutions.jts.geom.Geometry;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import org.junit.Assert;
import org.test.models.Cheminement;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class Test {

	@org.junit.Test
	public void test1() throws Exception {
		ShapeEngine shapeEngine = new ShapeEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V1.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V1.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		for (Cheminement cheminement : cheminementMap.values()) {
			Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "1");
			Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "2");
		}
		System.out.println("done");
	}

	@org.junit.Test
	public void test2() throws Exception {
		ShapeEngine shapeEngine = new ShapeEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V2.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V2.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		for (Cheminement cheminement : cheminementMap.values()) {
			if(cheminement.getFeature().getID().equals("CHEM_V2.2")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "1");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "2");
			}else if(cheminement.getFeature().getID().equals("CHEM_V2.1")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "2");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "3");
			}
		}
		System.out.println("done");
	}
	@org.junit.Test
	public void test3() throws Exception {
		ShapeEngine shapeEngine = new ShapeEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V3.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V3.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		for (Cheminement cheminement : cheminementMap.values()) {
			if(cheminement.getFeature().getID().equals("CHEM_V3.3")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "1");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V3.1");
			}else if(cheminement.getFeature().getID().equals("CHEM_V3.1")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V3.3");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "2");
			}else if(cheminement.getFeature().getID().equals("CHEM_V3.2")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "2");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "3");
			}
		}
		System.out.println("done");
	}
	@org.junit.Test
	public void test4() throws Exception {
		ShapeEngine shapeEngine = new ShapeEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V4.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V4.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		for (Cheminement cheminement : cheminementMap.values()) {
			if(cheminement.getFeature().getID().equals("CHEM_V4.4")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "1");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V4.2");
			}else if(cheminement.getFeature().getID().equals("CHEM_V4.1")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V4.2");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "2");
			}else if(cheminement.getFeature().getID().equals("CHEM_V4.2")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V4.4");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V4.1");
			}else if(cheminement.getFeature().getID().equals("CHEM_V4.3")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "2");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "3");
			}
		}
		System.out.println("done");
	}

	@org.junit.Test
	public void test5() throws Exception {
		ShapeEngine shapeEngine = new ShapeEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V5.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V5.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		for (Cheminement cheminement : cheminementMap.values()) {
			if(cheminement.getFeature().getID().equals("CHEM_V5.4")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V5.6");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V5.3");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.1")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V5.2");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "4");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.2")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V5.5");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V5.1");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.3")){
				Assert.assertEquals(cheminement.getCheminenement1().getFeature().getID(), "CHEM_V5.4");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "2");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.10")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "6");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "8");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.11")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "6");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "7");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.8")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "4");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "5");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.5")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "2");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V5.2");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.9")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "5");
				Assert.assertEquals(cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue(), "6");
			}else if(cheminement.getFeature().getID().equals("CHEM_V5.6")){
				Assert.assertEquals(cheminement.getNoeud1().getFeature().getProperty("NUMERO").getValue(), "1");
				Assert.assertEquals(cheminement.getCheminenement2().getFeature().getID(), "CHEM_V5.4");
			}
		}
		System.out.println("done");
	}

	@org.junit.Test
	public void testGps() throws Exception {
		ShapeEngine shapeEngine = new ShapeEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V5.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V5.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		JsonObject entries = shapeEngine.transformToGpsCoordinnate();
		System.out.println(entries.encodePrettily());
	}
}
