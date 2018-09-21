package org.test;

import com.vividsolutions.jts.geom.Geometry;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.asyncsql.PostgreSQLClient;
import io.vertx.reactivex.ext.sql.SQLClient;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.test.models.Cheminement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
		dbEngine.firstStep(list).subscribe( x -> {
			System.out.println(entries.encodePrettily());
			async.complete();
		}, error -> context.fail((Throwable) error) );
	}

	@Test
	public void testCable(TestContext context) {
		Async async = context.async();
		InputStream is = getClass().getClassLoader().getResourceAsStream("firstStep.json");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String result = reader.lines().collect(Collectors.joining("\n"));
		JsonObject paylaod = new JsonObject(result);
		CableGen cableGen = new CableGen(paylaod);
		cableGen.process(0).subscribe(x-> async.complete(), error -> context.fail(error));
	}
}
