package org.gooru.media.responses.writers;

import io.vertx.core.AsyncResult;
import io.vertx.ext.web.RoutingContext;

public class ResponseWriterBuilder {
  private final AsyncResult<Object> message;
  private final RoutingContext routingContext;

  public ResponseWriterBuilder(RoutingContext routingContext, AsyncResult<Object> message) {
    if (routingContext == null || message == null) {
      throw new IllegalArgumentException("Invalid or null routing context or message for Response Writer creation");
    }
    this.routingContext = routingContext;
    this.message = message;
  }

  public ResponseWriter build() {
    return new HttpServerResponseWriter(this.routingContext, this.message);
  }
}
