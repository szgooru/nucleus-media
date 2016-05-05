package org.gooru.media.utils;

import org.gooru.media.constants.MessageConstants;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by ashish on 30/12/15.
 */
public final class RouteRequestUtility {

    private RouteRequestUtility() {
        throw new AssertionError();
    }

    public static JsonObject getBodyForMessage(RoutingContext routingContext) {
        JsonObject result = new JsonObject();
        JsonObject httpBody = null;
        if (!routingContext.request().method().name().equals(HttpMethod.GET.name())) {
            httpBody = routingContext.getBodyAsJson();
        }
        if (httpBody != null) {
            result.put(MessageConstants.MSG_HTTP_BODY, httpBody);
        }
        result.put(MessageConstants.MSG_KEY_PREFS, (JsonObject) routingContext.get(MessageConstants.MSG_KEY_PREFS));
        result.put(MessageConstants.MSG_USER_ID, (String) routingContext.get(MessageConstants.MSG_USER_ID));
        return result;
    }
}
