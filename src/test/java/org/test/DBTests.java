package org.test;

import com.vividsolutions.jts.geom.Geometry;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.test.models.Cheminement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(VertxUnitRunner.class)
public class DBTests {

	private static Vertx vertx;
	private static SQLClient postgreSQLClient;

	@BeforeClass
	public static void setUp( TestContext context ) throws IOException
	{
		Async async = context.async();
		vertx = Vertx.vertx();
		ResourceFile s = new ResourceFile("/config.json");
		JsonObject config = new JsonObject(s.getContent());
		postgreSQLClient = PostgreSQLClient.createShared(vertx, config.getJsonObject("postgres"));
		App.postgreSQLClient = postgreSQLClient;
		async.complete();
	}

	@Test
	public void testDB(TestContext context) throws Exception {
		Async async = context.async();
		ShapeEngine shapeEngine = new ShapeEngine();
		GenEngine dbEngine = new GenEngine();
		URL resourceNoeud = getClass().getResource("/NEOUD_V5.shp");
		URL resourceCheminement = getClass().getResource("/CHEM_V5.shp");
		Map<Geometry, Cheminement> cheminementMap = shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(), 0);
		JsonObject entries = shapeEngine.transformToGpsCoordinnate();
		List<Object> list = shapeEngine.getFinalList();
		dbEngine.firstStep(list, x -> {
			System.out.println(entries.encodePrettily());
			async.complete();
		} );
	}

	@Test
	public void testCable(TestContext context) {
		Async async = context.async();
		InputStream is = getClass().getClassLoader().getResourceAsStream("firstStep.json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String result = reader.lines().collect(Collectors.joining("\n"));
		JsonObject paylaod = new JsonObject(result);
		CableGen cableGen = new CableGen(paylaod);
		Future<JsonObject> future = Future.future();
		future.setHandler(x -> {
			if(x.succeeded()){
				async.complete();
			}else {
				context.fail(x.cause());
			}
		});
		cableGen.process(0,future);
	}
}
