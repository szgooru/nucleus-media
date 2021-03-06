package org.gooru.media.routes;

import io.netty.handler.codec.http.HttpMethod;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import org.gooru.media.constants.ConfigConstants;
import org.gooru.media.constants.HttpConstants;
import org.gooru.media.constants.MessageConstants;
import org.gooru.media.constants.MessagebusEndpoints;
import org.gooru.media.constants.RouteConstants;
import org.gooru.media.responses.auth.AuthPrefsResponseHolderBuilder;
import org.gooru.media.responses.auth.AuthResponseHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteAuthConfigurator implements RouteConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger("org.gooru.media.bootstrap.FileUploadVerticle");

    @Override
    public void configureRoutes(Vertx vertx, Router router, JsonObject config) {

        EventBus eBus = vertx.eventBus();
        final long mbusTimeout = config.getLong(ConfigConstants.MBUS_TIMEOUT, 30L);

        router.route(RouteConstants.API_AUTH_ROUTE).handler(routingContext -> {

            String authorization = routingContext.request().getHeader(HttpConstants.HEADER_AUTH);
            String sessionToken = null;
            if (authorization != null && authorization.startsWith(HttpConstants.TOKEN)) {
                sessionToken = authorization.substring(HttpConstants.TOKEN.length()).trim();
            }

            // If the session token is null or absent, we send an error to
            // client
            if (sessionToken == null || sessionToken.isEmpty()) {
                routingContext.response().setStatusCode(HttpConstants.HttpStatus.UNAUTHORIZED.getCode())
                    .setStatusMessage(HttpConstants.HttpStatus.UNAUTHORIZED.getMessage()).end();
            } else {
                // If the session token is present, we send it to Message Bus
                // for validation
                DeliveryOptions options =
                    new DeliveryOptions().setSendTimeout(mbusTimeout * 1000)
                        .addHeader(MessageConstants.MSG_HEADER_OP, MessageConstants.MSG_OP_AUTH)
                        .addHeader(MessageConstants.MSG_HEADER_TOKEN, sessionToken);
                eBus.send(
                    MessagebusEndpoints.MBEP_AUTH,
                    null,
                    options,
                    reply -> {
                        if (reply.succeeded()) {
                            AuthResponseHolder responseHolder =
                                new AuthPrefsResponseHolderBuilder(reply.result()).build();
                            // Message header would indicate whether the auth
                            // was successful or not. In addition, successful
                            // auth may have been
                            // for anonymous user. We allow only GET request for
                            // anonymous user (since we do not support head,
                            // trace, options etc so far)
                            if (responseHolder.isAuthorized()) {
                                if (!routingContext.request().method().name().equals(HttpMethod.GET.name())
                                    && responseHolder.isAnonymous()) {
                                    routingContext.response()
                                        .setStatusCode(HttpConstants.HttpStatus.FORBIDDEN.getCode())
                                        .setStatusMessage(HttpConstants.HttpStatus.FORBIDDEN.getMessage()).end();
                                } else {
                                    LOG.debug("User authenticated, Fowarding request to next route.. ");
                                    routingContext.next();
                                }
                            } else {
                                routingContext.response()
                                    .setStatusCode(HttpConstants.HttpStatus.UNAUTHORIZED.getCode())
                                    .setStatusMessage(HttpConstants.HttpStatus.UNAUTHORIZED.getMessage()).end();
                            }
                        } else {
                            LOG.error("Not able to send message", reply.cause());
                            routingContext.response().setStatusCode(HttpConstants.HttpStatus.ERROR.getCode()).end();
                        }
                    });
            }
        });

    }

}
