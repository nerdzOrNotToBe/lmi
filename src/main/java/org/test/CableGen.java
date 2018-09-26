package org.test;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import io.reactivex.Single;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.test.models.*;

import java.text.DecimalFormat;
import java.util.*;

public class CableGen {

	private static Logger logger = LoggerFactory.getLogger(CableGen.class);


	private final WKTReader parser;
	private final GeometryFactory geometryFactory;
	private static DecimalFormat df2 = new DecimalFormat(".##");

	private JsonArray noeuds;
	private JsonArray cheminements;
	private JsonArray cables;
	private JsonArray cablelines;
	private JsonArray cabconds;

	private TCable cable;

	private Map<String, TCheminement> mapND1 = new HashMap<>();
	private Map<String, TCheminement> mapND2 = new HashMap<>();

	private String currentCode;

	public CableGen(JsonObject payload) {
		this.noeuds = payload.getJsonArray("noeuds");
		this.cheminements = payload.getJsonArray("cheminements");

		for (Object cheminement : cheminements) {

			TCheminement tCheminement = Json.decodeValue(((JsonObject) cheminement).encode(), TCheminement.class);
			mapND1.put(tCheminement.getCm_ndcode1(),tCheminement);
			mapND2.put(tCheminement.getCm_ndcode2(),tCheminement);
		}

		this.cables = new JsonArray();
		this.cablelines = new JsonArray();
		this.cabconds = new JsonArray();
		geometryFactory = JTSFactoryFinder.getGeometryFactory();
		parser = new WKTReader(geometryFactory);
	}

	public Single<JsonObject> process(int i){
		if(i == noeuds.size()){
			int j = 0;
			for (Object o : cables) {
				createCableline((JsonObject) o);
				createCableCond((JsonObject) o);
			}
			JsonObject result = new JsonObject();
			result.put("cables",cables);
			result.put("cablelines",cablelines);
			result.put("cabconds",cabconds);
			return Single.just(result);
		}else {
			TNoeud noeud = Json.decodeValue(this.noeuds.getJsonObject(i).encode(),TNoeud.class);
			if (noeud.isPointBranchement()) {
				if(i == 0) {
					newCable();
					return setCodeCable(++i, noeud);
				}else {
					cable.setCb_nd2(noeud.getNd_code());
					cables.add(JsonObject.mapFrom(cable));
					System.out.println(i);
					newCable();
					return setCodeCable(++i, noeud);
				}
			} else {
				return process(i + 1);
			}
		}
	}

	private void createCableCond(JsonObject o) {
		String cb_nd1 = o.getString("cb_nd1");
		String cb_nd2 = o.getString("cb_nd2");
		List<String> codeConduites = findConduite(cb_nd1, cb_nd2);
		for (String codeConduite : codeConduites) {
			TCabCond tCabCond = new TCabCond();
			tCabCond.setCc_cd_code(codeConduite);
			tCabCond.setCc_cb_code(o.getString("cb_code"));
			cabconds.add(JsonObject.mapFrom(tCabCond));
		}
	}

	private List<String> findConduite(String cb_nd1, String cb_nd2) {
		List<String> codes = new ArrayList<>();
		for (int i = 0; i < cheminements.size(); i++) {
			JsonObject chem = cheminements.getJsonObject(i);
			if(cb_nd1.equalsIgnoreCase(chem.getString("cm_ndcode1")) || i >0 && cb_nd1.equalsIgnoreCase(cheminements.getJsonObject(i-1).getString("cm_ndcode2"))){
				codes.add(chem.getString("cm_code").replace("CM","CD"));
			}else if( i >0 && cb_nd1.equalsIgnoreCase(cheminements.getJsonObject(i-1).getString("cm_ndcode2"))){
				codes.add(cheminements.getJsonObject(i-1).getString("cm_code").replace("CM","CD"));
			}else if (cb_nd2.equalsIgnoreCase(chem.getString("cm_ndcode2"))){
				codes.add(chem.getString("cm_code").replace("CM","CD"));
				break;
			}else if (codes.size() > 0){
				codes.add(chem.getString("cm_code").replace("CM","CD"));
			}
		}
		return codes;
	}

	private void createCableline(JsonObject o) {
		TCableline tCableline = new TCableline();
		tCableline.setCl_code( o.getString("cb_code").replace("CB","CL"));
		tCableline.setCl_cb_code( o.getString("cb_code"));
		String cb_nd1 = o.getString("cb_nd1");
		String cb_nd2 = o.getString("cb_nd2");
		LineString line = findLine(cb_nd1, cb_nd2);
		tCableline.setGeom(line.toText());
		Coordinate[] coordinates = line.getCoordinates();
		try {
			CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
			double total = 0;
			for (int c = 0; c < coordinates.length - 1; c++) {
				Coordinate c1 = coordinates[c];
				Coordinate c2 = coordinates[c + 1];

				total += JTS.orthodromicDistance(c1, c2, crs2154);
			}
			tCableline.setCl_long(String.valueOf(df2.format(total)));

		} catch (FactoryException | TransformException e) {
			logger.error("Cannot calculate Long of cheminement");
		}
		cablelines.add(JsonObject.mapFrom(tCableline));
		System.out.println(tCableline.getCl_long());
		System.out.println(line);
	}

	private LineString findLine(String cb_nd1, String cb_nd2) {
		List<Coordinate> coordinates = new ArrayList<>();
		for (int i = 0; i < cheminements.size(); i++) {
			JsonObject chem = cheminements.getJsonObject(i);
			if(cb_nd1.equalsIgnoreCase(chem.getString("cm_ndcode1"))){
				addCoordinatesRevert(chem.getString("geom"), coordinates);
			}else if( i >0 && cb_nd1.equalsIgnoreCase(cheminements.getJsonObject(i-1).getString("cm_ndcode2"))){
				addCoordinatesRevert(cheminements.getJsonObject(i-1).getString("geom"), coordinates);
			}else if (cb_nd2.equalsIgnoreCase(chem.getString("cm_ndcode2"))){
				addCoordinatesRevert(chem.getString("geom"), coordinates);
				break;
			}else if (coordinates.size() > 0){
				addCoordinates(chem.getString("geom"), coordinates);
			}
		}
		Coordinate[] coordinatesArray = new Coordinate[coordinates.size()];
		return geometryFactory.createLineString( coordinates.toArray(coordinatesArray));
	}

	private void addCoordinates(String geom, List<Coordinate> coordinates) {
		try {
			MultiLineString lineString = (MultiLineString) parser.read(geom);

			makeListCoordinates(coordinates, lineString);
		} catch (ParseException e) {
			logger.error("error when we try to read coordinates", e);
		}
	}
	private void addCoordinatesRevert(String geom, List<Coordinate> coordinates) {
		try {
			MultiLineString lineString = (MultiLineString) parser.read(geom);
			if(cablelines.size()==0 && coordinates.size() == 0){
				makeListCoordinates(coordinates, lineString);
			}else if(cablelines.size() == 0) {
				if (coordinates.size() > 0 && coordinates.get(coordinates.size() - 1).equals2D(lineString.getGeometryN(lineString.getNumGeometries()-1).getCoordinates()[lineString.getGeometryN(lineString.getNumGeometries()-1).getCoordinates().length-1])){
					makeRevertCoordinates(coordinates, lineString);
				} else {
					makeListCoordinates(coordinates, lineString);
				}
			}else {
				LineString lastCableGeom = (LineString) parser.read(cablelines.getJsonObject(cablelines.size() - 1).getString("geom"));
				if (coordinates.size() == 0 && lastCableGeom.getCoordinates()[lastCableGeom.getCoordinates().length - 1].equals2D(lineString.getGeometryN(lineString.getNumGeometries()-1).getCoordinates()[lineString.getGeometryN(lineString.getNumGeometries()-1).getCoordinates().length-1])) {
					makeRevertCoordinates(coordinates, lineString);
				}else {
					makeListCoordinates(coordinates, lineString);
				}
			}
		} catch (ParseException e) {
			logger.error("error when we try to read coordinates", e);
		}
	}

	private void makeRevertCoordinates(List<Coordinate> coordinates, MultiLineString lineString) {
		for (int i = lineString.getNumGeometries() - 1; i >= 0; i--) {
			LineString geometryN = (LineString) lineString.getGeometryN(i);
			List<Coordinate> list = Arrays.asList(geometryN.getCoordinates());
			Collections.reverse(list);
			coordinates.addAll(list);
		}
	}

	private void makeListCoordinates(List<Coordinate> coordinates, MultiLineString lineString) {
		for (int i = 0; i < lineString.getNumGeometries(); i++) {
			LineString geometryN = (LineString) lineString.getGeometryN(i);
			List<Coordinate> list = Arrays.asList(geometryN.getCoordinates());
			coordinates.addAll(list);
		}
	}

	private void newCable() {
		cable = new TCable();
		cable.setCb_prop("OR900000000002");
		cable.setCb_gest("OR900000000014");
		cable.setCb_proptyp("CST");
		cable.setCb_statut("PRO");
		cable.setCb_tech("OPT");
		cable.setCb_typephy("C");
		cable.setCb_modulo(12L);
	}

	private Single<JsonObject> setCodeCable(int i, TNoeud noeud) {
		return getCodeCable(noeud).flatMap(getCodeCable -> {
				cable.setCb_code(getCodeCable);
				cable.setCb_nd1(noeud.getNd_code());
				return process(i);
		});
	}

	private Single<String> getCodeCable(TNoeud noeud) {
		String codeStart = noeud.getNd_code().replace("ND", "CB");
		codeStart = codeStart.substring(0, 9);
		if(currentCode == null || !currentCode.equalsIgnoreCase(codeStart)) {
			String table = "data.t_cable";
			String field = "cb_code";
			String codeKey = "cable";
			currentCode = codeStart;
			return GenEngine.nextCode( codeStart, table, field, codeKey);
		}else {
			return Single.just(GenEngine.getAndIncreaseCode("cable"));
		}
	}

}
