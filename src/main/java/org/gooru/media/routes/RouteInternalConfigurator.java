package org.gooru.media.routes;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;

class RouteInternalConfigurator implements RouteConfigurator {


  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    router.route("/banner").handler(routingContext -> {
      JsonObject result = new JsonObject().put("Organisation", "gooru.org").put("Product", "auth").put("purpose", "authentication")
                                          .put("mission", "Honor the human right to education");
      routingContext.response().end(result.toString());
    });

    final MetricsService metricsService = MetricsService.create(vertx);
    router.route("/metrics").handler(routingContext -> {
      JsonObject ebMetrics = metricsService.getMetricsSnapshot(vertx);
      routingContext.response().end(ebMetrics.toString());
    });
  }
}
