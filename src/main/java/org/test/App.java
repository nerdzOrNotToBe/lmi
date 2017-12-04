package org.test;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Hello world!
 */
public class App extends AbstractVerticle {
	
	private Logger logger = LoggerFactory.getLogger(App.class);

	private ConcurrentMap<String, JsonObject> sessions = new ConcurrentHashMap<>();
	
	SQLClient postgreSQLClient;
	@Override
	public void start() {

		postgreSQLClient = PostgreSQLClient.createShared(vertx, config().getJsonObject("postgres"));

		Router router = Router.router(vertx);
		router.route().handler(CorsHandler.create("http://localhost:4200")
				.allowedMethod(HttpMethod.GET)
				.allowedMethod(HttpMethod.POST)
				.allowedMethod(HttpMethod.PUT)
				.allowedMethod(HttpMethod.DELETE)
				.allowedMethod(HttpMethod.OPTIONS)
				.allowedHeader("Content-Type").allowCredentials(true));
		// Enable multipart form data parsing
		router.route().handler(BodyHandler.create().setUploadsDirectory("uploads"));
		// handle the form
		router.post("/upload/noeud").handler(ctx -> {
			ctx.response().putHeader("Content-Type", "text/plain");

			ctx.response().setChunked(true);

			for (FileUpload f : ctx.fileUploads()) {
				vertx.executeBlocking(future -> {
					try {
						unZipIt(f.uploadedFileName(),"uploads"+File.separator+"noeud"+File.separator+f.fileName().replace(".zip",""));
						future.complete();
					} catch (IOException e) {
						future.fail(e);
					}
				},result -> {
					if(result.succeeded()) {
						ctx.response().end();
					}else {
						ctx.fail(result.cause());
					}
				});
			}

			ctx.response().end();
		});		// handle the form
		router.post("/upload/cheminement").handler(ctx -> {
			ctx.response().putHeader("Content-Type", "text/plain");

			ctx.response().setChunked(true);

			for (FileUpload f : ctx.fileUploads()) {
				vertx.executeBlocking(future -> {
					try {
						unZipIt(f.uploadedFileName(),"uploads"+File.separator+"cheminement"+File.separator+f.fileName().replace(".zip",""));
						future.complete();
					} catch (IOException e) {
						future.fail(e);
					}
				},result -> {
					if(result.succeeded()) {
						ctx.response().end();
					}else {
						ctx.fail(result.cause());
					}
				});
			}

		});
		// handle the form
		router.get("/shapes/noeud").handler(ctx -> {
			vertx.fileSystem().readDir("uploads"+File.separator+"noeud",read -> {
				writeShapesList(ctx, read);
			});
		});
		// handle the form
		router.get("/shapes/cheminement").handler(ctx -> {
			vertx.fileSystem().readDir("uploads"+File.separator+"cheminement",read -> {
				writeShapesList(ctx, read);
			});
		});

		router.post("/shapes/firstStep").handler(this::firstStep);
		router.post("/shapes/secondStep").handler(this::secondStep);

		vertx.createHttpServer().requestHandler(router::accept).listen(8080);
		//URL resourceNoeud = getClass().getResource("/noeud.shp");
		//URL resourceCheminement = getClass().getResource("/t_cheminement.shp");
		//shapeEngine.process(resourceNoeud.getFile(),resourceCheminement.getFile(),3);
	}

	private void firstStep(RoutingContext routingContext) {
		JsonObject payload = routingContext.getBodyAsJson();
		ShapeEngine shapeEngine = new ShapeEngine();
		DBEngine dbEngine = new DBEngine(postgreSQLClient);
		String pathNoeud = "uploads"+File.separator+"noeud"+File.separator+payload.getString("noeud")+File.separator+payload.getString("noeud")+".shp";
		String pathCheminement = "uploads"+File.separator+"cheminement"+File.separator+payload.getString("cheminement")+File.separator+payload.getString("cheminement")+".shp";
		JsonObject shapeEngineConfig = new JsonObject();
		shapeEngineConfig.put("pathNoeud",pathNoeud);
		shapeEngineConfig.put("pathCheminement",pathCheminement);
		sessions.put(payload.getString("token"),shapeEngineConfig);
		vertx.executeBlocking(future -> {
			try {
				shapeEngine.process(pathNoeud,pathCheminement, 0);
				dbEngine.firstStep(shapeEngine.getFinalList(), x->{
					future.complete(x.result());
				});
			} catch (Exception e) {
				future.fail(e);
			}
		}, result -> {
			if(result.succeeded()){
				routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON);
				JsonObject jsonResult = new JsonObject();
				jsonResult.put("map", shapeEngine.transformToGpsCoordinnate());
				jsonResult.put("noeuds", ((JsonObject)result.result()).getJsonArray("noeuds"));
				jsonResult.put("cheminements",  ((JsonObject)result.result()).getJsonArray("cheminements"));
				jsonResult.put("pointsTech",  ((JsonObject)result.result()).getJsonArray("pointsTech"));
				jsonResult.put("sitesTech",  ((JsonObject)result.result()).getJsonArray("sitesTech"));
				jsonResult.put("adresses",  ((JsonObject)result.result()).getJsonArray("adresses"));
				jsonResult.put("ebps",  ((JsonObject)result.result()).getJsonArray("ebps"));
				routingContext.response().end(Json.encode(jsonResult));
			}else {
				routingContext.fail(result.cause());
			}
		});


	}
	private void secondStep(RoutingContext routingContext) {
		JsonObject payload = routingContext.getBodyAsJson();
		JsonObject shapeEngineConfig = sessions.get(payload.getString("token"));
		DBEngine dbEngine = new DBEngine(postgreSQLClient);
		dbEngine.secondStep(payload.getJsonObject("data"), x -> {
			if(x.succeeded()){
				routingContext.response().putHeader(HttpHeaders.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON);
				JsonObject jsonResult = new JsonObject();
				jsonResult.put("conduites",x.result().getJsonArray("conduites"));
				jsonResult.put("cond_chems",x.result().getJsonArray("cond_chems"));
				jsonResult.put("cables",x.result().getJsonArray("cables"));
				jsonResult.put("cablelines",x.result().getJsonArray("cablelines"));
				jsonResult.put("cabconds",x.result().getJsonArray("cabconds"));
				routingContext.response().end(Json.encode(jsonResult));
			}else {
				routingContext.fail(x.cause());
			}
		});

	}

	private void writeShapesList(RoutingContext ctx, AsyncResult<List<String>> read) {
		if(read.succeeded()){
			ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
			ArrayList<String> list = new ArrayList<>();
			for (String s : read.result()) {
				String[] split = s.split("\\"+File.separator);
				list.add(split[split.length-1]);
			}
			ctx.response().end(Json.encode(list));
		}else {
			ctx.fail(read.cause());
		}
	}

	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param outputFolder zip file output folder
	 */
	public void unZipIt(String zipFile, String outputFolder) throws IOException {

		byte[] buffer = new byte[1024];


			File folder = new File(outputFolder);
			if(!folder.exists()){
				folder.mkdir();
			}

			//get the zip file content
			ZipInputStream zis =
					new ZipInputStream(new FileInputStream(zipFile));
			//get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while(ze!=null){

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				logger.info("file unzip : "+ newFile.getAbsoluteFile());

				//create all non exists folders
				//else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();
			Files.deleteIfExists(new File(zipFile).toPath());
			logger.info("Done");

	}
}
