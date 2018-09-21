package org.test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.ext.sql.SQLClient;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InsertEngine {
	private Logger logger = LoggerFactory.getLogger(InsertEngine.class);
	private SQLClient sqlClient;

	NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

	public InsertEngine(SQLClient sqlClient) {
		this.sqlClient = sqlClient;
	}

	public void process(JsonObject payload, Handler<AsyncResult<Void>> h) {
		Future future = Future.future();
		future.setHandler(h);
		sqlClient.getConnection(connectionResult -> {
			if(connectionResult.succeeded()){
				connectionResult.result().setAutoCommit(false, beginTrans -> {
					try {
						List<String> queries = new ArrayList<>();
						queries.addAll(generateQueriesNoeud(payload.getJsonArray("noeuds")));
						queries.addAll(generateQueriesCheminement(payload.getJsonArray("cheminements")));
						queries.addAll(generateQueriesPTs(payload.getJsonArray("pts")));
						queries.addAll(generateQueriesAdresses(payload.getJsonArray("adresses")));
						queries.addAll(generateQueriesSTs(payload.getJsonArray("sts")));
						queries.addAll(generateQueriesEBPs(payload.getJsonArray("ebps")));
						queries.addAll(generateQueriesConduites(payload.getJsonArray("conduites")));
						queries.addAll(generateQueriesCondChems(payload.getJsonArray("cond_chems")));
						queries.addAll(generateQueriesCable(payload.getJsonArray("cables")));
						queries.addAll(generateQueriesCablelines(payload.getJsonArray("cablelines")));
						queries.addAll(generateQueriesCabConds(payload.getJsonArray("cab_conds")));
						StringBuilder multiQueries = new StringBuilder();
						for (String query : queries) {
							multiQueries.append(query).append('\n');
						}
						connectionResult.result().execute(multiQueries.toString(), execute -> {
							if(execute.succeeded()){
							connectionResult.result().commit(commit -> {
								if(commit.succeeded()){
									future.complete();

								}else {
									future.fail(commit.cause());
									logger.error("Commit failed",commit.cause());
								}
							});
//								connectionResult.result().rollback(rollback ->{
//									if(rollback.succeeded()){
//
//										future.complete();
//										logger.error("Rollback done");
//									}else {
//										future.fail(rollback.cause());
//										logger.error("Rollback error", rollback.cause());
//									}
//									connectionResult.result().close();
//								});
							}else {
								connectionResult.result().rollback(rollback ->{
									if(rollback.succeeded()){

										future.fail(execute.cause());
										logger.error("Rollback done",execute.cause());
									}else {
										future.fail(rollback.cause());
										logger.error("Rollback error", rollback.cause());
									}
									connectionResult.result().close();
								});
							}
						});


					} catch (Exception e) {
						logger.error("strange error ", e);
						future.fail(e);
					}

				});
			} else {
				future.fail(connectionResult.cause());
			}
		});
	}

	private List<String> generateQueriesCabConds(JsonArray cab_conds) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object c : cab_conds) {
			JsonObject n = (JsonObject) c;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_cab_cond (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("cc_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesCablelines(JsonArray cablelines) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object c : cablelines) {
			JsonObject n = (JsonObject) c;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_cableline (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("cl_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesCable(JsonArray cables) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object c : cables) {
			JsonObject n = (JsonObject) c;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_cable (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("cb_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesCondChems(JsonArray cond_chems) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object c : cond_chems) {
			JsonObject n = (JsonObject) c;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_cond_chem (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("dm_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesConduites(JsonArray conduites) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object c : conduites) {
			JsonObject n = (JsonObject) c;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_conduite (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("cd_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesEBPs(JsonArray ebps) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object ebp : ebps) {
			JsonObject n = (JsonObject) ebp;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_ebp (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("bp_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesAdresses(JsonArray adresses) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object ad : adresses) {
			JsonObject n = (JsonObject) ad;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_adresse (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("ad_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesSTs(JsonArray sts) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object st : sts) {
			JsonObject n = (JsonObject) st;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_sitetech (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("st_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesPTs(JsonArray pts) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object pt : pts) {
			JsonObject n = (JsonObject) pt;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_ptech (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("pt_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}

	private List<String> generateQueriesCheminement(JsonArray cheminements) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object chem : cheminements) {
			JsonObject n = (JsonObject) chem;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_cheminement (");
			foundFieldsAndValues(n, values, query);
			/* creation date */
			query.append("cm_creadat").append(")");
			if(fillValues(values, query)) {
				queries.add(query.toString());
			}
		}
		return queries;
	}



	private List<String> generateQueriesNoeud(JsonArray noeuds) throws ParseException {
		List<String> queries = new ArrayList<>();
		for (Object noeud : noeuds) {
			JsonObject n = (JsonObject) noeud;
			List<Object> values = new ArrayList<>();
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO data.t_noeud (");
			foundFieldsAndValues(n, values, query);
			// creation date
			query.append("nd_creadat").append(")");
			if(fillValues(values, query)){
				queries.add(query.toString());
			}
		}
		return queries;
	}


	private boolean fillValues(List<Object> values, StringBuilder query) {
		query.append(" VALUES (");
		boolean allNull = true;
		for (Object value : values) {
			if(value != null){
				allNull = false;
			}
			if(value instanceof String){
				if(((String) value).contains("POINT") ||  ((String) value).contains("LINESTRING")){
					query.append("ST_SetSRID(")
							.append("ST_GeomFromText('")
							.append(value)
							.append("')")
							.append(",2154)")
							.append(",");
				}else {
					query.append("'")
							.append(value)
							.append("'")
							.append(",");
				}
			}else {
				query.append(value)
						.append(",");
			}
		}
		query.append("current_timestamp )");
		query.append(";");
		return !allNull;
	}

	private void foundFieldsAndValues(JsonObject n, List<Object> values, StringBuilder query) throws ParseException {
		for (String field : n.fieldNames()){
			if(n.getValue(field) != null && !field.equalsIgnoreCase("pointBranchement")){
				if(n.getValue(field) instanceof String && !((String) n.getValue(field)).isEmpty()){
					if(field.contains("_long") || field.contains("_larg") || field.contains("_charge")){
						Number number = format.parse(n.getString(field));
						values.add(number.doubleValue());
					} else if (((String) n.getValue(field)).contains("MULTILINESTRING"))  {
						String linestring = ((String) n.getValue(field)).replace("MULTILINESTRING ((", "LINESTRING (");
						values.add(linestring.replace("))", ")"));
					} else {
						values.add(n.getValue(field));
					}
				}else {
					values.add(n.getValue(field));
				}
				query.append(field).append(",");
			}
		}
	}
}
