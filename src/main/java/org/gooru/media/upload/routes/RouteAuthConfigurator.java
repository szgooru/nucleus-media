package org.gooru.media.upload.routes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class RouteAuthConfigurator implements RouteConfigurator {

  static final Logger LOG = LoggerFactory.getLogger("org.gooru.media.upload.bootstrap.FileUploadVerticle");

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    
  }

}
