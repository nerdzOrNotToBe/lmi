package org.test;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.sql.SQLClient;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.test.models.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GenEngine {
	private static Logger logger = LoggerFactory.getLogger(GenEngine.class);


	private static Map<String, String> codes = new HashMap<>();
	private Map<String, TNoeud> tNoeudMap = new HashMap<>();
	private Map<String, TPtech> tPtMap = new HashMap<>();
	private Map<String, TSitetech> sitetechMap = new HashMap<>();
	private static DecimalFormat df2 = new DecimalFormat(".##");
	private JsonObject dataFill = new JsonObject();
	private String currentNroSro = "";
	private Boolean nroSroChanged = false;

	public GenEngine() {}

	public void firstStep(List<Object> objects, Handler<AsyncResult<JsonObject>> handler) {
		Future<JsonObject> f = Future.future();
		f.setHandler(handler);
	    AtomicInteger i = new AtomicInteger(0);
		codes = new HashMap<>();

		process(objects, f, i);


	}

	private void process(List<Object> objects, Future<JsonObject> f, AtomicInteger i) {
		processNext(objects, i.get(), x -> {
			if(x.succeeded()){
				i.addAndGet(1);
				if(i.get()<objects.size()) {
					process(objects, f, i);
				}else {
					JsonArray noeuds = new JsonArray();
					JsonArray cheminements = new JsonArray();
					JsonArray pts = new JsonArray();
					JsonArray siteTechs = new JsonArray();
					JsonArray adresses = new JsonArray();
					JsonArray ebps = new JsonArray();
					for (Object object : objects) {
						if (object instanceof Noeud) {
							Noeud noeud = (Noeud) object;
							TNoeud tNoeud = createNoeud(noeud);
							if ("ST".equals(tNoeud.getNd_type())) {
								TSitetech siteTech = createSiteTech(tNoeud, noeud, noeuds, pts);
								siteTechs.add(JsonObject.mapFrom(siteTech));
								TAdresse tAdresse = createAdresse(tNoeud, noeud, noeuds);
								adresses.add(JsonObject.mapFrom(tAdresse));
								// to calculate t_cable and t_cableline
								tNoeud.setPointBranchement(true);
							}else {
								TPtech pointTech = createPointTech(tNoeud, noeud, noeuds, pts);
								pts.add(JsonObject.mapFrom(pointTech));
								TEbp tEbp = createEbp(tNoeud, noeud, noeuds, pointTech);
								if (noeud.getFeature().getProperty("BPE").getValue() != null && !((String) noeud.getFeature().getProperty("BPE").getValue()).isEmpty()) {
									ebps.add(JsonObject.mapFrom(tEbp));
									// to calculate t_cable and t_cableline
									tNoeud.setPointBranchement(true);
								}
								if (noeud.getFeature().getProperty("BPE1").getValue() != null && !((String) noeud.getFeature().getProperty("BPE1").getValue()).isEmpty()) {
									TEbp tEbp1 = createEbp1(tNoeud, noeud, noeuds, pointTech);
									ebps.add(JsonObject.mapFrom(tEbp1));
									// to calculate t_cable and t_cableline
									tNoeud.setPointBranchement(true);
								}
							}
							noeuds.add(JsonObject.mapFrom(tNoeud));
						} else {
							TCheminement tCheminement = createCheminement((Cheminement) object);
							cheminements.add(JsonObject.mapFrom(tCheminement));
						}
					}
					dataFill.put("cheminements", cheminements);
					dataFill.put("noeuds", noeuds);
					dataFill.put("pointsTech", pts);
					dataFill.put("sitesTech", siteTechs);
					dataFill.put("adresses", adresses);
					dataFill.put("ebps", ebps);
					f.complete(dataFill);
				}
			}else {
				f.fail(x.cause());
			}
		});
	}

	private void processNext(List<Object> objects, Integer i, Handler<AsyncResult<Void>> resultHandler) {
		Future f = Future.future();
		f.setHandler(resultHandler);
		Object current = objects.get(i);
		if (current instanceof Noeud) {
			getCodeNoeud(current, x -> {
				if(x.succeeded()){
					if(nroSroChanged) {
						((Noeud) current).setCode(codes.get("noeud"));
					}else {
						((Noeud) current).setCode(getAndIncreaseCode("noeud"));
					}
					f.complete();
				}
			});
		} else {
			getCodeCheminement(x -> {
				((Cheminement) current).setCode(codes.get("cheminement"));
				f.complete();
			});
		}
	}

	private TEbp createEbp(TNoeud tNoeud, Noeud object, JsonArray noeuds, TPtech pointTech) {
		TEbp tEbp = new TEbp();
		tEbp.setBp_code(tNoeud.getNd_code().replace("ND", "BP"));
		String bpe = (String) object.getFeature().getProperty("BPE").getValue();
		if (bpe.length() < 3) {
			while (bpe.length() < 3) {
				bpe = 0 + bpe;
			}
		}
		bpe = "B" + bpe;
		tEbp.setBp_typephy(bpe);
		commonBPE(pointTech, tEbp);
		return tEbp;
	}

	private TEbp createEbp1(TNoeud tNoeud, Noeud object, JsonArray noeuds, TPtech pointTech) {
		TEbp tEbp = new TEbp();
		String code = tNoeud.getNd_code().replace("ND", "BP");
		Integer numero = Integer.valueOf(code.substring(9)) + 5;
		tEbp.setBp_code(code.substring(0, 9) + numero);
		String bpe1 = (String) object.getFeature().getProperty("BPE1").getValue();
		if (bpe1.length() < 3) {
			while (bpe1.length() < 3) {
				bpe1 = 0 + bpe1;
			}
		}
		bpe1 = "B" + bpe1;
		tEbp.setBp_typephy(bpe1);
		commonBPE(pointTech, tEbp);
		return tEbp;
	}

	private void commonBPE(TPtech pointTech, TEbp tEbp) {
		tEbp.setBp_pt_code(pointTech.getPt_code());
		tEbp.setBp_prop(pointTech.getPt_prop());
		tEbp.setBp_gest(pointTech.getPt_gest());
		tEbp.setBp_proptyp(pointTech.getPt_proptyp());
		tEbp.setBp_statut(pointTech.getPt_statut());
		tEbp.setBp_typelog("BPE");
	}

	private TAdresse createAdresse(TNoeud tNoeud, Noeud noeud, JsonArray noeuds) {
		TAdresse tAdresse = new TAdresse();
		tAdresse.setAd_code(tNoeud.getNd_code().replace("ND", "AD"));
		tAdresse.setGeom(tNoeud.getGeom());
		Geometry geometry = (Geometry) noeud.getFeature().getDefaultGeometry();
		tAdresse.setAd_x_ban(geometry.getCoordinate().x);
		tAdresse.setAd_y_ban(geometry.getCoordinate().y);
		return tAdresse;
	}

	private TSitetech createSiteTech(TNoeud tNoeud, Noeud noeud, JsonArray noeuds, JsonArray pts) {
		TSitetech tSitetech = new TSitetech();
		tSitetech.setSt_code(tNoeud.getNd_code().replace("ND", "ST"));
		tSitetech.setSt_nd_code(tNoeud.getNd_code());
		String calque = (String) noeud.getFeature().getProperty("CALQUE").getValue();
		if (calque.equals("Chambres à poser")) {
			tSitetech.setSt_prop("OR900000000002");
			tSitetech.setSt_gest("OR900000000014");
		} else {
			tSitetech.setSt_prop("OR900000000000");
			tSitetech.setSt_gest("OR900000000000");
		}
		String nom_bloc = (String) noeud.getFeature().getProperty("NOM_BLOC").getValue();
		switch (nom_bloc) {
			case "NRA":
				tSitetech.setSt_typephy("BAT");
				tSitetech.setSt_typelog("NRA");
				break;
			case "PRM":
				tSitetech.setSt_typephy("ADR");
				tSitetech.setSt_typelog("NRAMED");
				break;
			case "SR":
				tSitetech.setSt_typephy("ADR");
				tSitetech.setSt_typelog("SRP");
				break;
		}
		tSitetech.setSt_statut("PRO");
		tSitetech.setSt_ad_code(tNoeud.getNd_code().replace("ND", "AD"));

		//on corrige le tnoeud precedent pour mettre de l OCC
		if(noeuds.size() > 0 && pts.size() > 0){
			pts.getJsonObject(pts.size() -1 ).put("pt_proptyp", "OCC");
		}
		return tSitetech;
	}

	private TPtech createPointTech(TNoeud tnoeud, Noeud noeud, JsonArray noeuds, JsonArray pts) {
		TPtech tPtech = new TPtech();
		tPtech.setPt_code(tnoeud.getNd_code().replace("ND", "PT"));
		tPtech.setPt_nd_code(tnoeud.getNd_code());
		String calque = (String) noeud.getFeature().getProperty("CALQUE").getValue();
		if (calque.equals("Chambres à poser")) {
			tPtech.setPt_prop("OR900000000002");
			tPtech.setPt_gest("OR900000000014");
			tPtech.setPt_proptyp("CST");
		} else {
			tPtech.setPt_prop("OR900000000000");
			tPtech.setPt_gest("OR900000000000");
			if (noeuds.size() == 1) {
				// on est en OCC is le premier noeud est ST
				if (noeuds.getJsonObject(noeuds.size() - 1).getString("nd_type").equals("ST")) {
					tPtech.setPt_proptyp("OCC");
				} else {
					tPtech.setPt_proptyp("LOC");
				}
			} else {
				tPtech.setPt_proptyp("LOC");
			}
		}
		String nom_bloc = (String) noeud.getFeature().getProperty("NOM_BLOC").getValue();
		if (nom_bloc.equals("POTEAU")) {
			tPtech.setPt_typephy("P");
		} else {
			tPtech.setPt_typephy("C");
		}
		String BPE = (String) noeud.getFeature().getProperty("BPE").getValue();
		if (BPE != null && !BPE.isEmpty()) {
			tPtech.setPt_typelog("R");
			tPtech.setPt_a_passa("1");
		} else {
			tPtech.setPt_typelog("T");
			tPtech.setPt_a_passa("0");
		}
		tPtech.setPt_nature(nom_bloc);
		tPtech.setPt_statut("PRO");
		return tPtech;
	}

	private TCheminement createCheminement(Cheminement cheminement) {
		TCheminement tCheminement = new TCheminement();
		tCheminement.setCm_code(cheminement.getCode());
		if (cheminement.getNoeud1() != null) {
			tCheminement.setCm_ndcode1(cheminement.getNoeud1().getCode());
		}
		if (cheminement.getNoeud2() != null) {
			tCheminement.setCm_ndcode2(cheminement.getNoeud2().getCode());
		}
		if (cheminement.getCheminenement1() != null) {
			tCheminement.setCm_cm1(cheminement.getCheminenement1().getCode());
		}
		if (cheminement.getCheminenement2() != null) {
			tCheminement.setCm_cm2(cheminement.getCheminenement2().getCode());
		}
		String calque = (String) cheminement.getFeature().getProperty("CALQUE").getValue();
		switch (calque) {
			case "RE_Fourreaux existants":
				tCheminement.setCm_avct("E");
				break;
			default:
				tCheminement.setCm_avct("C");
		}
		if (tCheminement.getCm_avct().equals("E")) {
			tCheminement.setInfra_type("4.7");
		}
		tCheminement.setCm_statut("PRO");
		tCheminement.setCm_typelog("TR");
		tCheminement.setCm_typ_imp("7");
		tCheminement.setCm_nature("TEL");
		tCheminement.setS_nominale("1");
		WKTWriter wktWriter = new WKTWriter();
		Geometry geometry = (Geometry) cheminement.getFeature().getDefaultGeometry();
		Coordinate[] coordinates = geometry.getCoordinates();
		try {
			CoordinateReferenceSystem crs2154 = CRS.decode("EPSG:2154");
			double total = 0;
			for (int c = 0; c < coordinates.length - 1; c++) {
				Coordinate c1 = coordinates[c];
				Coordinate c2 = coordinates[c + 1];

				total += JTS.orthodromicDistance(c1, c2, crs2154);
			}
			tCheminement.setCm_long(String.valueOf(df2.format(total)));

		} catch (FactoryException | TransformException e) {
			logger.error("Cannot calculate Long of cheminement");
		}
		String geomString = wktWriter.write(geometry);
		tCheminement.setGeom(geomString);
		return tCheminement;
	}

	private TNoeud createNoeud(Noeud noeud) {
		TNoeud tNoeud = new TNoeud();
		tNoeud.setNd_code(noeud.getCode());
		String nom_bloc = (String) noeud.getFeature().getProperty("NOM_BLOC").getValue();
		switch (nom_bloc) {
			case "NRA":
			case "PRM":
			case "SR":
				tNoeud.setNd_type("ST");
				break;
			default:
				tNoeud.setNd_type("PT");
		}

		tNoeud.setNd_type_ep("OPT");
		tNoeud.setS_nominale("1");
		WKTWriter wktWriter = new WKTWriter();
		Geometry geometry = (Geometry) noeud.getFeature().getDefaultGeometry();
		String geomString = wktWriter.write(geometry);
		tNoeud.setGeom(geomString);
		return tNoeud;
	}

	public static String getAndIncreaseCode(String currentCode) {
		String value = String.valueOf(codes.get(currentCode));
		String root = codes.get(currentCode).substring(0, 9);
		Integer number = Integer.valueOf(codes.get(currentCode).substring(9));
		number += 10;
		codes.put(currentCode, root + number);
		return  root + number;
	}

//	private void getCodeCable(Handler<AsyncResult<String>> resultHandler) {
//		Future<String> future = Future.future();
//		future.setHandler(resultHandler);
//		if(nroSroChanged) {
//
//			String codeStart = codes.get("noeud").replace("ND", "CB");
//			codeStart = codeStart.substring(0, 9);
//			getNumero(codeStart, "data.t_cable", "cb_code", numeroResult -> {
//				if (numeroResult.succeeded()) {
//					future.complete(numeroResult.result());
//				} else {
//					future.fail(numeroResult.cause());
//				}
//			});
//		}else {
//			future.complete(getAndIncreaseCode("cable"));
//		}
//	}

	private void getCodeCheminement(Handler<AsyncResult<String>> resultHandler) {
		Future<String> future = Future.future();
		future.setHandler(resultHandler);
		if(nroSroChanged) {
			nroSroChanged = false;
			String codeStart = codes.get("noeud").replace("ND", "CM");
			codeStart = codeStart.substring(0, 9);
			String table = "data.t_cheminement";
			String field = "cm_code";
			String codeKey = "cheminement";
			nextCode(future, codeStart, table, field, codeKey);
		}else {
			future.complete(getAndIncreaseCode("cheminement"));
		}
	}

	private void getCodeNoeud(Object object, Handler<AsyncResult<Void>> resultHandler) {
		Future f = Future.future();
		f.setHandler(resultHandler);
		Noeud firstNoeud = (Noeud) object;
		WKTWriter wktWriter = new WKTWriter();
		Geometry geometry = (Geometry) firstNoeud.getFeature().getDefaultGeometry();
		String geomString = wktWriter.write(geometry);
		getZnro(geomString, codeRno -> {
			if (codeRno.succeeded()) {
				getZsro(geomString, codeRno.result(), codeSro -> {
					if (codeSro.succeeded()) {
						if(currentNroSro.equals(codeSro.result())){
							nroSroChanged =false;
							f.complete();
						}else {
							currentNroSro = codeSro.result();
							nroSroChanged =true;
							String table = "data.t_noeud";
							String field = "nd_code";
							String codeKey = "noeud";
							nextCode(f, currentNroSro, table, field, codeKey);
						}
					} else {
						logger.error(codeSro.cause());
						f.fail(codeSro.cause());
					}
				});
			} else {
				logger.error(codeRno.cause());
				f.fail(codeRno.cause());
			}
		});
	}

	public static void nextCode(Future f, String codeSro, String table, String field, String codeKey) {
		if(codes.containsKey(codeKey)){
			Integer num = Integer.parseInt(codes.get(codeKey).substring(9)) + 10;
			String code = codeSro + num;
			String query = "SELECT * FROM " + table + " WHERE " + field + " LIKE '" + code + "%' ORDER BY " + field + " DESC";
			App.postgreSQLClient.getConnection(connection -> {
				if (connection.succeeded()) {
					connection.result().query(query, queryResult -> {
						if(queryResult.succeeded()){
							if(queryResult.result().getRows().size() > 0){
								getAndPutNumero(f, codeSro, table, field, codeKey);
							}else {
								codes.put(codeKey, code);
								f.complete(code);
							}
						}else {
							f.fail(queryResult.cause());
						}
						connection.result().close();
					});
				}else {
					f.fail(connection.cause());
				}
			});
		}else {
			getAndPutNumero(f, codeSro, table, field, codeKey);
		}
	}

	public static void getAndPutNumero(Future f, String codeSro, String table, String field, String codeKey) {
		getNumero(codeSro, table, field, codeFinal -> {
			if (codeFinal.succeeded()) {
				codes.put(codeKey, codeFinal.result());
				f.complete(codeFinal.result());
			} else {
				logger.error(codeFinal.cause());
				f.fail(codeFinal.cause());
			}
		});
	}

	private void getZnro(String geomString, Handler<AsyncResult<String>> resultHandler) {
		String codeStart = "ND";
		Future<String> future = Future.future();
		future.setHandler(resultHandler);
		App.postgreSQLClient.getConnection(connection -> {
			String queryznro = "SELECT * FROM public.znro WHERE  ST_Contains(geom,ST_GeomFromText('" + geomString + "',2154)) ='t';";
			connection.result().query(queryznro, znroResult -> {
				if (znroResult.succeeded()) {
					String code = codeStart;
					List<JsonObject> znroResults = znroResult.result().getRows();
					if (znroResults.size() > 1) {
						System.out.println("Bizarre match plusieurs znro");
					}
					if (znroResults.size() > 0) {
						JsonObject row = znroResults.get(0);
						code = code.concat(row.getString("code_nro").substring(2, 4));// departement
						code = code.concat(row.getString("code_chiff"));
						connection.result().close();
						future.complete(code);
					}
				} else {
					connection.result().close();
					future.fail(znroResult.cause());
				}
			});
		});
	}

	private void getZsro(String geomString, String codeStart, Handler<AsyncResult<String>> resultHandler) {
		Future<String> future = Future.future();
		future.setHandler(resultHandler);
		String queryzsro = "SELECT * FROM public.zsro WHERE  ST_Contains(geom,ST_GeomFromText('" + geomString + "',2154)) ='t';";
		App.postgreSQLClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query(queryzsro, zsroResult -> {
					if (zsroResult.succeeded()) {
						String code = codeStart;
						List<JsonObject> zsroResults = zsroResult.result().getRows();
						if (zsroResults.size() > 1) {
							System.out.println("Bizarre match plusieurs znro");
						}
						if (zsroResults.size() > 0) {
							JsonObject rowZsro = zsroResults.get(0);
							code = code.concat(rowZsro.getString("code_sro").substring(9));
							connection.result().close();
							future.complete(code);
						}
					} else {
						connection.result().close();
						future.fail(zsroResult.cause());
					}
				});
			} else {
				future.fail(connection.cause());
			}
		});
	}

	public static void getNumero(String codeStart, String table, String field, Handler<AsyncResult<String>> resultHandler) {
		final String[] code = {codeStart + "7"};
		Future<String> future = Future.future();
		future.setHandler(resultHandler);
		String query = "SELECT * FROM " + table + " WHERE " + field + " LIKE '" + code[0] + "%' ORDER BY " + field + " DESC";
		App.postgreSQLClient.getConnection(connection -> {
			if (connection.succeeded()) {
				connection.result().query(query, queryResult -> {
					if (queryResult.succeeded()) {
						List<JsonObject> rows = queryResult.result().getRows();
						if (rows.size() > 0) {
							JsonObject rowZsro = rows.get(0);
							int nd_codeEnd = Integer.parseInt(rowZsro.getString(field).substring(10));
							nd_codeEnd = nd_codeEnd + 100;
							String codeEnd = String.valueOf(nd_codeEnd);
							if (nd_codeEnd < 1000){
								codeEnd = "0" + codeEnd;
							}
							code[0] = code[0].concat(codeEnd);

							connection.result().close();
							future.complete(code[0]);
						} else {
							connection.result().close();
							future.complete(code[0] + "0010");
						}
					} else {
						connection.result().close();
						future.fail(queryResult.cause());
					}
				});
			} else {
				future.fail(connection.cause());
			}
		});
	}

	public void secondStep(JsonObject data, Handler<AsyncResult<JsonObject>> handler) {
		Future<JsonObject> future = Future.future();
		future.setHandler(handler);
		for (Object o : data.getJsonArray("noeuds")) {
			TNoeud tNoeud = Json.decodeValue(Json.encode(o), TNoeud.class);
			tNoeudMap.put(tNoeud.getNd_code(), tNoeud);
		}

		for (Object o : data.getJsonArray("pts")) {
			TPtech tPtech = Json.decodeValue(Json.encode(o), TPtech.class);
			tPtMap.put(tPtech.getPt_code(), tPtech);
		}

		for (Object o : data.getJsonArray("sts")) {
			TSitetech tSitetech = Json.decodeValue(Json.encode(o), TSitetech.class);
			sitetechMap.put(tSitetech.getSt_code(), tSitetech);
		}
		JsonArray conduites = new JsonArray();
		JsonArray cond_chems = new JsonArray();

		for (Object o : data.getJsonArray("cheminements")) {
			TCheminement tCheminement = Json.decodeValue(Json.encode(o), TCheminement.class);
			if (tCheminement.getCm_code() == null) {
				continue;
			}

			Integer nbConduites = 2;
			if (tCheminement.getCm_avct().equals("C") && !tCheminement.getCm_mod_pos().equals("TRA")) {
				nbConduites = 3;
			}
			for (int i = 0; i < nbConduites; i++) {
				TConduite conduite = createConduite(tCheminement, i);
				conduites.add(JsonObject.mapFrom(conduite));

				TCondChem tCondChem = new TCondChem();
				tCondChem.setDm_cd_code(conduite.getCd_code());
				tCondChem.setDm_cm_code(tCheminement.getCm_code());
				cond_chems.add(JsonObject.mapFrom(tCondChem));
			}

		}
		CableGen cableGen = new CableGen(data);
		Future<JsonObject> f = Future.future();
		f.setHandler( x -> {
			JsonObject result = new JsonObject();
			result.put("conduites", conduites);
			result.put("cond_chems", cond_chems);
			result.put("cables", x.result().getJsonArray("cables"));
			result.put("cablelines",  x.result().getJsonArray("cablelines"));
			result.put("cabconds",  x.result().getJsonArray("cabconds"));
			future.complete(result);
		});
		cableGen.process(0, f );

	}


	private TConduite createConduite(TCheminement tCheminement, int i) {
		TConduite tConduite = new TConduite();
		Integer numero = Integer.valueOf(tCheminement.getCm_code().substring(9)) + i;
		String code = tCheminement.getCm_code().replace("CM", "CD");
		tConduite.setCd_code(code.substring(0, 9) + numero);
		if (tCheminement.getCm_avct().equals("E")) {
			tConduite.setCd_prop("OR900000000000");
			tConduite.setCd_gest("OR900000000000");
			tConduite.setCd_type("NC");
			if (tCheminement.getCm_ndcode1() != null && tNoeudMap.get(tCheminement.getCm_ndcode1()).getNd_type().equals("ST")) {
				tConduite.setCd_proptyp("OCC");
			} else if (tCheminement.getCm_ndcode2() != null && tNoeudMap.get(tCheminement.getCm_ndcode2()).getNd_type().equals("ST")) {
				tConduite.setCd_proptyp("OCC");
			} else {
				tConduite.setCd_proptyp("LOC");
			}
		} else {
			tConduite.setCd_prop("OR900000000002");
			tConduite.setCd_gest("OR900000000014");
			tConduite.setCd_proptyp("CST");
			if (tCheminement.getCm_mod_pos().equals("TRA")) {
				tConduite.setCd_type("PVC");
				tConduite.setCd_dia_int(57L);
				tConduite.setCd_dia_ext(60L);
			} else {
				tConduite.setCd_type("PEHD");
				tConduite.setCd_dia_int(33L);
				tConduite.setCd_dia_ext(40L);
			}
		}
		tConduite.setCd_avct(tCheminement.getCm_avct());
		tConduite.setCd_long(tCheminement.getCm_long());
		tConduite.setCd_statut("PRO");
		return tConduite;
	}
}
