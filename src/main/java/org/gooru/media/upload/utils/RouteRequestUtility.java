package org.gooru.media.upload.utils;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.gooru.media.upload.constants.MessageConstants;

/**
 * Created by ashish on 30/12/15.
 */
public class RouteRequestUtility {


  public JsonObject getBodyForMessage(RoutingContext routingContext) {
    JsonObject result = new JsonObject();
    JsonObject httpBody = null;
    if (!routingContext.request().method().name().equals(HttpMethod.GET.name())) {
      httpBody = routingContext.getBodyAsJson();
    }
    if (httpBody != null) {
      result.put(MessageConstants.MSG_HTTP_BODY, httpBody);
    }
    result.put(MessageConstants.MSG_KEY_PREFS, (JsonObject)routingContext.get(MessageConstants.MSG_KEY_PREFS));
    result.put(MessageConstants.MSG_USER_ID, (String)routingContext.get(MessageConstants.MSG_USER_ID));
    return result;
  }
}
