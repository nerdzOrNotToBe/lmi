package org.test;

import com.vividsolutions.jts.geom.Geometry;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.test.models.Cheminement;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

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
		async.complete();
	}

	@Test
	public void testDB(TestContext context) throws Exception {
		Async async = context.async();
		ShapeEngine shapeEngine = new ShapeEngine();
		GenEngine dbEngine = new GenEngine(postgreSQLClient);
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
}
