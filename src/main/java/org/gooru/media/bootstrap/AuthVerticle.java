package org.gooru.media.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import org.gooru.media.bootstrap.shutdown.Finalizer;
import org.gooru.media.bootstrap.shutdown.Finalizers;
import org.gooru.media.bootstrap.startup.Initializers;
import org.gooru.media.bootstrap.startup.Initializer;
import org.gooru.media.constants.MessageConstants;
import org.gooru.media.constants.MessagebusEndpoints;
import org.gooru.media.infra.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(AuthVerticle.class);
    public static final String ACCESS_TOKEN_VALIDITY = "access_token_validity";

    @Override
    public void start(Future<Void> voidFuture) throws Exception {
        vertx.executeBlocking(blockingFuture -> {
            startApplication();
        }, future -> {
            if (future.succeeded()) {
                voidFuture.complete();
            } else {
                voidFuture.fail("Not able to initialize the auth handlers machiners properly");
            }
        });
        EventBus eb = vertx.eventBus();
        eb.localConsumer(
            MessagebusEndpoints.MBEP_AUTH,
            message -> {
                LOG.debug("Received message: " + message.body());
                vertx.executeBlocking(
                    future -> {
                        JsonObject result = getAccessToken(message.headers().get(MessageConstants.MSG_HEADER_TOKEN));
                        future.complete(result);
                    },
                    res -> {
                        if (res.result() != null) {
                            JsonObject result = (JsonObject) res.result();
                            DeliveryOptions options =
                                new DeliveryOptions().addHeader(MessageConstants.MSG_OP_STATUS,
                                    MessageConstants.MSG_OP_STATUS_SUCCESS);
                            message.reply(result, options);
                        } else {
                            message.reply(null);
                        }
                    });

            }).completionHandler(result -> {
            if (result.succeeded()) {
                LOG.info("Auth end point ready to listen");
            } else {
                LOG.error("Error registering the auth handler. Halting the Auth machinery");
                Runtime.getRuntime().halt(1);
            }
        });
    }

    @Override
    public void stop() throws Exception {
        shutDownApplication();
        super.stop();
    }

    private void startApplication() {
        Initializers initializers = new Initializers();
        try {
            for (Initializer initializer : initializers) {
                initializer.initializeComponent(vertx, config());
            }
        } catch (IllegalStateException ie) {
            LOG.error("Error initializing application", ie);
            Runtime.getRuntime().halt(1);
        }
    }

    private void shutDownApplication() {
        Finalizers finalizers = new Finalizers();
        for (Finalizer finalizer : finalizers) {
            finalizer.finalizeComponent();
        }
    }

    private JsonObject getAccessToken(String token) {
        JsonObject accessToken = RedisClient.instance().getJsonObject(token);
        if (accessToken != null) {
            int expireAtInSeconds = accessToken.getInteger(ACCESS_TOKEN_VALIDITY);
            RedisClient.instance().expire(token, expireAtInSeconds);
        }
        return accessToken;
    }
}
