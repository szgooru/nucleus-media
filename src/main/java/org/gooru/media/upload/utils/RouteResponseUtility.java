package org.gooru.media.upload.utils;

import org.gooru.media.upload.constants.ErrorsConstants;
import org.gooru.media.upload.constants.HttpConstants.HttpStatus;
import org.gooru.media.upload.responses.writers.ResponseWriterBuilder;
import org.slf4j.Logger;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by ashish on 30/12/15.
 */
public class RouteResponseUtility {


  public void responseHandler(final RoutingContext routingContext, final AsyncResult<Object> reply,
                               final Logger LOG) {
    if (reply.succeeded()) {
      new ResponseWriterBuilder(routingContext, reply).build().writeResponse();
    } else {
      int statusCode = routingContext.statusCode();
      if(statusCode == HttpStatus.TOO_LARGE.getCode()){
        routingContext.response().setStatusCode(HttpStatus.TOO_LARGE.getCode()).end(HttpStatus.TOO_LARGE.getMessage());
      }
      else{
        LOG.error("Not able to send message", reply.cause());
        routingContext.response().setStatusCode(500).end();
      }
      
    }
  }
  
  public void errorResponseHandler(final RoutingContext routingContext, final Logger LOG, final String response, final int statusCode){
    LOG.error("Field upload failed : " + response);
    JsonObject error = new JsonObject();
    error.put("type", ErrorsConstants.UploadErrorType.VALIDATION.getType());
    error.put("errors", response);
    routingContext.response().setStatusCode(statusCode);
    routingContext.response().end(response);
  }
}
