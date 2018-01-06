package org.test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTWriter;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.util.Pair;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.test.models.Cheminement;
import org.test.models.Noeud;
import org.test.models.TCheminement;
import org.test.models.TNoeud;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.*;

public class ShapeEngine implements Serializable {

	private Map<Geometry, Noeud> noeudMap = new HashMap<>();
	private Map<Geometry, Cheminement> cheminenementMap = new HashMap<>();
	private SimpleFeatureSource noeudFeatureSource;
	private SimpleFeatureSource cheminementFeatureSource;
	private List<SimpleFeature> noeudFeatures;
	private List<SimpleFeature> cheminementsFeatures = new ArrayList<>();
	private JsonObject gps = new JsonObject();


	public Map<Geometry, Cheminement> process(String noeudFile, String cheminementFile, Integer firstNoeud) throws Exception {
		extractNoeuds(noeudFile);
		extractCheminements(cheminementFile);
		associateNoeudCheminement(noeudMap.get(noeudFeatures.get(firstNoeud).getDefaultGeometryProperty().getValue()));
		return cheminenementMap;
	}

	private void extractNoeuds(String resource) throws IOException {
		File file = new File(resource);
		Map<String, URL> map = new HashMap<String, URL>();
		map.put("url", file.toURI().toURL());

		DataStore dataStore = DataStoreFinder.getDataStore(map);

		noeudFeatureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
		FeatureCollection<SimpleFeatureType, SimpleFeature> features1 = noeudFeatureSource.getFeatures();
		try (FeatureIterator<SimpleFeature> features = features1.features()) {
			ArrayList<SimpleFeature> list = new ArrayList<>();
			while (features.hasNext()) {
				list.add(features.next());
			}
			Collections.sort(list, (o1, o2) -> {
				Integer num1 = Integer.parseInt((String) o1.getProperty("NUMERO").getValue());
				Integer num2 = Integer.parseInt((String) o2.getProperty("NUMERO").getValue());
				return num1.compareTo(num2);
			});
			noeudFeatures = list;
			// cree tous les noeuds en base
			list.forEach(feature -> {
				GeometryAttribute geom = feature.getDefaultGeometryProperty();
				Geometry geomValue = (Geometry) geom.getValue();
				noeudMap.put(geomValue, new Noeud(feature));
			});
		}

	}

	private void extractCheminements(String resource) throws IOException {
		File file = new File(resource);
		Map<String, URL> map = new HashMap<String, URL>();
		map.put("url", file.toURI().toURL());

		DataStore dataStore = DataStoreFinder.getDataStore(map);

		cheminementFeatureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);


		FeatureCollection<SimpleFeatureType, SimpleFeature> features1 = cheminementFeatureSource.getFeatures();
		try (FeatureIterator<SimpleFeature> features = features1.features()) {
			ArrayList<SimpleFeature> list = new ArrayList<>();
			while (features.hasNext()) {
				SimpleFeature feature = features.next();
				cheminementsFeatures.add(feature);
				GeometryAttribute geom = feature.getDefaultGeometryProperty();
				Geometry geomValue = (Geometry) geom.getValue();
				cheminenementMap.put(geomValue, new Cheminement(feature));
			}
		}
	}

	private void associateNoeudCheminement(Noeud currentNoeud) {
		boolean firstNoeud = true;
		GeometryAttribute geom = currentNoeud.getFeature().getDefaultGeometryProperty();
		Geometry geomValue = (Geometry) geom.getValue();
		for (Map.Entry<Geometry, Cheminement> geometryCheminenementEntry : cheminenementMap.entrySet()) {
			if (geomValue.touches(geometryCheminenementEntry.getKey()) && !containHimSelf(geomValue, geometryCheminenementEntry.getValue()) && notfull(geometryCheminenementEntry.getValue())) {
				if (firstNoeud) {
					geometryCheminenementEntry.getValue().setNoeud1(noeudMap.get(geomValue));
					Noeud nextNoeud = searchNextNoeud(noeudMap.get(geomValue), geometryCheminenementEntry.getValue());
					if (nextNoeud != null) {
						geometryCheminenementEntry.getValue().setNoeud2(nextNoeud);
						associateNoeudCheminement(nextNoeud);
					} else {
						Cheminement cheminement = searchNextCheminement(noeudMap.get(geomValue), geometryCheminenementEntry.getValue());
						if (cheminement != null) {
							geometryCheminenementEntry.getValue().setCheminenement2(cheminement);
							cheminement.setCheminenement1(geometryCheminenementEntry.getValue());
							associateCheminementNoeud2(cheminement, null);
						}
					}
				}
			}
		}

	}

	private void associateCheminementCheminement(Cheminement cheminement) {
		for (Map.Entry<Geometry, Cheminement> current : cheminenementMap.entrySet()) {
			if (current.getKey().intersects((Geometry) cheminement.getFeature().getDefaultGeometryProperty().getValue()) && !containHimSelf(current.getKey(), cheminement) && notfull(current.getValue()) ) {
				cheminement.setCheminenement2(current.getValue());
				Noeud nextNoeud = searchNextNoeud(noeudMap.get(current.getKey()), cheminement);
				if (nextNoeud != null) {
					current.getValue().setNoeud1(nextNoeud);
					associateCheminementNoeud2(current.getValue(), nextNoeud);
				} else {
					current.getValue().setCheminenement1(cheminement);
					associateCheminementNoeud2(current.getValue(), nextNoeud);
				}
			}
		}
	}

	private void associateCheminementNoeud2(Cheminement current, Noeud currentNoeud) {
		Noeud noeud = searchNextNoeud(currentNoeud, current);
		if (noeud != null) {
			current.setNoeud2(noeud);
			associateNoeudCheminement(noeud);
		} else {
			associateCheminementCheminement(current);
		}

	}

	private Noeud searchNextNoeud(Noeud noeud, Cheminement currentCheminements) {
		boolean found = false;
		for (SimpleFeature noeudFeature : noeudFeatures) {
			GeometryAttribute geom = noeudFeature.getDefaultGeometryProperty();
			Geometry geomValue = (Geometry) geom.getValue();
			if (geomValue.intersects((Geometry) currentCheminements.getFeature().getDefaultGeometryProperty().getValue()) && !containHimSelf(geomValue, currentCheminements) ) {
				return noeudMap.get(geomValue);
			}

		}
		return null;
	}

	private Cheminement searchNextCheminement(Noeud noeud, Cheminement currentCheminements) {
		for (Map.Entry<Geometry, Cheminement> current : cheminenementMap.entrySet()) {
			if (current.getKey().intersects((Geometry) currentCheminements.getFeature().getDefaultGeometryProperty().getValue()) && !containHimSelf(current.getKey(), currentCheminements) && notfull(current.getValue())) {
				return current.getValue();
			}
		}
		return null;
	}

	private boolean notfull(Cheminement c) {
		int allreadySet= 0;
		if(c.getCheminenement1() != null){
			allreadySet++;
		}
		if (c.getCheminenement2() != null){
			allreadySet++;
		}
		if (c.getNoeud1() != null){
			allreadySet++;
		}
		if (c.getNoeud2() != null){
			allreadySet++;
		}

		return allreadySet<2;
	}


	private boolean containHimSelf(Geometry geo, Cheminement currentCheminement) {
		if (currentCheminement.getNoeud1() != null && ((Geometry) currentCheminement.getNoeud1().getFeature().getDefaultGeometryProperty().getValue()).equals(geo)) {
			return true;
		}
		if (currentCheminement.getNoeud2() != null && ((Geometry) currentCheminement.getNoeud2().getFeature().getDefaultGeometryProperty().getValue()).equals(geo)) {
			return true;
		}
		if (currentCheminement.getCheminenement1() != null && ((Geometry) currentCheminement.getCheminenement1().getFeature().getDefaultGeometryProperty().getValue()).equals(geo)) {
			return true;
		}
		if (currentCheminement.getCheminenement2() != null && ((Geometry) currentCheminement.getCheminenement2().getFeature().getDefaultGeometryProperty().getValue()).equals(geo)) {
			return true;
		}
		if (((Geometry) currentCheminement.getFeature().getDefaultGeometryProperty().getValue()).equals(geo)) {
			return true;
		}
		return false;
	}


	public JsonObject transformToGpsCoordinnate() {
		try {
			CoordinateReferenceSystem sourceCrs = CRS.decode("EPSG:2154");
			CoordinateReferenceSystem targetCrs = CRS.decode("EPSG:4326");
			boolean lenient = true;
			MathTransform mathTransform = CRS.findMathTransform(sourceCrs, targetCrs, lenient);
			JsonArray noeuds = new JsonArray();
			gps.put("noeuds", noeuds);
			for (SimpleFeature noeudFeature : noeudFeatures) {
				Geometry defaultGeometry = (Geometry) noeudFeature.getDefaultGeometry();
				DirectPosition2D srcDirectPosition2D
						= new DirectPosition2D(sourceCrs, defaultGeometry.getCoordinate().x, defaultGeometry.getCoordinate().y);
				DirectPosition2D destDirectPosition2D
						= new DirectPosition2D();
				mathTransform.transform(srcDirectPosition2D, destDirectPosition2D);
				JsonObject noeud = new JsonObject();
				noeud.put("lat", destDirectPosition2D.x);
				noeud.put("lng", destDirectPosition2D.y);
				noeud.put("id", noeudMap.get(defaultGeometry).getCode());
				noeuds.add(noeud);
			}
			JsonArray cheminements = new JsonArray();
			gps.put("cheminements", cheminements);
			for (SimpleFeature cheminementFeature : cheminementsFeatures) {
				Geometry defaultGeometry = (Geometry) cheminementFeature.getDefaultGeometry();
				JsonArray coordinnates = new JsonArray();
				for (int i = 0; i < defaultGeometry.getCoordinates().length; i++) {
					Coordinate coordinate = defaultGeometry.getCoordinates()[i];
					DirectPosition2D srcDirectPosition2D
							= new DirectPosition2D(sourceCrs, coordinate.x, coordinate.y);
					DirectPosition2D destDirectPosition2D
							= new DirectPosition2D();
					mathTransform.transform(srcDirectPosition2D, destDirectPosition2D);
					JsonObject point = new JsonObject();
					point.put("lng", destDirectPosition2D.y);
					point.put("lat", destDirectPosition2D.x);
					coordinnates.add(point);
				}
				JsonObject cheminement = new JsonObject();
				cheminement.put("coordinnates", coordinnates);
				cheminement.put("id", cheminenementMap.get(defaultGeometry).getCode());
				cheminements.add(cheminement);
			}
		} catch (FactoryException e) {
			e.printStackTrace();
		} catch (TransformException e) {
			e.printStackTrace();
		}
		return gps;
	}


	public List<Object> getFinalList() {
		List<Object> objects = new ArrayList<>();
		for (Noeud noeud : noeudMap.values()) {
			if( noeud.getFeature().equals(noeudFeatures.get(0))){
				objects.add(noeud);
				break;
			}
		}
		for (Cheminement cheminement : cheminenementMap.values()) {
			if(cheminement.getNoeud1() != null && cheminement.getNoeud1().getFeature().equals(((Noeud)objects.get(0)).getFeature())){
				objects.add(cheminement);
				break;
			}
		}
		findNextObject(objects.get(1), objects);
		Noeud previousNoeud = null;
		Cheminement previousCheminement = null;
		for (int i = 0; i < objects.size(); i++) {
			Object o = objects.get(i);
			if(o instanceof Cheminement){
				Cheminement current = (Cheminement) o;
				if(current.getCheminenement1() == null && previousCheminement != null){
					current.setCheminenement1(previousCheminement);
				}
				findNextNoeudAndCheminement(current, i+1, objects);
				previousCheminement = (Cheminement) o;
			}


		}
		return  objects;
	}

	private void findNextNoeudAndCheminement(Cheminement current, int i,List<Object> objects) {
		boolean noeud2Found = false;
		for (int j = i; j < objects.size(); j++) {
			Object o = objects.get(j);
			if(o instanceof Noeud){
				if(current.getNoeud2() == null && !noeud2Found){
					current.setNoeud2((Noeud) o);
					noeud2Found = true;
				}
			}else {
				if(current.getCheminenement2() == null &&  current.getNoeud2() != null && ((Cheminement) o).getNoeud1() != null) {
					Object o2 = objects.get(i-2);
					if (o2 instanceof Noeud && ((Cheminement) o).getNoeud1().getFeature().equals(((Noeud) o2).getFeature())) {
						return;
					}
				}
				if(current.getCheminenement2() == null){
					current.setCheminenement2((Cheminement) o);
				}
			}
			if(current.getCheminenement2() != null){
				return;
			}
		}
	}

	private void findNextObject(Object o, List<Object> objects) {
		if(((Cheminement)o).getNoeud2() != null){
			objects.add(((Cheminement)o).getNoeud2());
			List<Pair<Integer,Cheminement>> nextPossibilities = new ArrayList<>();
			for (Cheminement cheminement : cheminenementMap.values()) {
				if(cheminement.getNoeud1() != null && cheminement.getNoeud1().getFeature().equals(((Cheminement)o).getNoeud2().getFeature())){
					if(cheminement.getNoeud2() != null) {
						nextPossibilities.add(new Pair<>(Integer.parseInt((String) cheminement.getNoeud2().getFeature().getProperty("NUMERO").getValue()), cheminement));
					}else {
						nextPossibilities.add(new Pair<>(Integer.MAX_VALUE, cheminement));
					}
				}
			}
			Collections.sort(nextPossibilities, Comparator.comparing(Pair::getKey));
			for (Pair<Integer, Cheminement> nextPossibility : nextPossibilities) {
				objects.add(nextPossibility.getValue());
				findNextObject(nextPossibility.getValue(), objects);
			}
		}else {
			objects.add(((Cheminement)o).getCheminenement2());
			findNextObject(((Cheminement)o).getCheminenement2(), objects);
		}
	}
}
