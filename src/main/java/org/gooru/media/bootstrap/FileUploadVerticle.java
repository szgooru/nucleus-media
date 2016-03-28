package org.gooru.media.bootstrap;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import org.gooru.media.constants.ConfigConstants;
import org.gooru.media.routes.RouteConfiguration;
import org.gooru.media.routes.RouteConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(FileUploadVerticle.class);

  @Override
  public void start() throws Exception {

    LOG.info("Starting FileUploadVerticle...");

    final HttpServer httpServer = vertx.createHttpServer();

    final Router router = Router.router(vertx);

    DeploymentOptions options = new DeploymentOptions().setConfig(config());

    vertx.deployVerticle("org.gooru.media.bootstrap.AuthVerticle", options, res -> {
      if (res.succeeded()) {
        LOG.info("Deploying AuthVerticle... " + res.result());
      } else {
        LOG.info("Deployment of AuthVerticle failed !" + res.cause());
      }
    });

    // Register the routes
    RouteConfiguration rc = new RouteConfiguration();
    for (RouteConfigurator configurator : rc) {
      configurator.configureRoutes(vertx, router, config());
    }

    // If the port is not present in configuration then we end up
    // throwing as we are casting it to int. This is what we want.
    final int port = config().getInteger(ConfigConstants.HTTP_PORT);
    LOG.info("Http server starting on port {}", port);
    httpServer.requestHandler(router::accept).listen(port, result -> {
      if (result.succeeded()) {
        LOG.info("HTTP Server started successfully");
      } else {
        // Can't do much here, Need to Abort. However, trying to exit may have us blocked on other threads that we may have spawned, so we need to use
        // brute force here
        LOG.error("Not able to start HTTP Server", result.cause());
        Runtime.getRuntime().halt(1);
      }
    });

  }

}
