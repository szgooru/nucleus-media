package org.gooru.media.routes;

import org.gooru.media.constants.ConfigConstants;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

class RouteGlobalConfigurator implements RouteConfigurator {

    @Override
    public void configureRoutes(Vertx vertx, Router router, JsonObject config) {

        final long bodyLimit = config.getLong(ConfigConstants.MAX_FILE_SIZE);

        final String uploadLocation = config.getString(ConfigConstants.UPLOAD_LOCATION);

        BodyHandler bodyHandler = BodyHandler.create().setBodyLimit(bodyLimit).setUploadsDirectory(uploadLocation);

        router.route().handler(bodyHandler);

    }

}
