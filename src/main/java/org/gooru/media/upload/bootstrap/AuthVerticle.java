package org.gooru.media.upload.bootstrap;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthVerticle extends AbstractVerticle {

  private static final String MBEP_AUTH = "org.gooru.media.upload.auth";
  private static final String MSG_HEADER_OP = "mb.operation";
  private static final String MSG_HEADER_TOKEN = "session.token";
  private static final String MSG_OP_AUTH = "auth";
  private static final String MSG_OP_STATUS = "mb.operation.status";
  private static final String MSG_OP_STATUS_SUCCESS = "success";
  private static final String MSG_OP_STATUS_ERROR = "error";
  private static final String REDIS_CONFIGURATION_KEY = "redis.config";
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthVerticle.class);
  private RedisClient redisClient;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    EventBus eb = vertx.eventBus();
    initializeVerticle(startFuture);

    if (startFuture.failed()) {
      return;
    }
    eb.localConsumer(MBEP_AUTH, message -> {

      String msgOp = message.headers().get(MSG_HEADER_OP);
      String sessionToken = message.headers().get(MSG_HEADER_TOKEN);
      LOGGER.info("Message received ! " + sessionToken);
      final DeliveryOptions deliveryOptions = new DeliveryOptions();

      if (sessionToken != null && !sessionToken.isEmpty()) {
        if (msgOp.equalsIgnoreCase(MSG_OP_AUTH)) {
          redisClient.get(sessionToken, redisGetAsyncHandler -> {
            JsonObject result = null;
            if (redisGetAsyncHandler.succeeded()) {

              if (redisGetAsyncHandler.result() != null) {
                result = new JsonObject(redisGetAsyncHandler.result());
                deliveryOptions.addHeader(MSG_OP_STATUS, MSG_OP_STATUS_SUCCESS);
              } else {
                LOGGER.info("Session not found. Invalid session");
                deliveryOptions.addHeader(MSG_OP_STATUS, MSG_OP_STATUS_ERROR);
              }
            } else {
              LOGGER.error("Redis operation failed", redisGetAsyncHandler.cause());
              deliveryOptions.addHeader(MSG_OP_STATUS, MSG_OP_STATUS_ERROR);
            }
            message.reply(result, deliveryOptions);

          });
        }
      } else {
        LOGGER.error("Unable to authorize. Invalid authorization header");
        deliveryOptions.addHeader(MSG_OP_STATUS, MSG_OP_STATUS_ERROR);
        message.reply(new JsonObject(), deliveryOptions);
      }
    }).completionHandler(result -> {
      if (result.succeeded()) {
        LOGGER.info("Auth end point ready to listen");
        startFuture.complete();
      } else {
        LOGGER.error("Error registering the auth handler. Halting the auth machinery");
        startFuture.fail("Error registering the auth handler. Halting the auth machinery");
        Runtime.getRuntime().halt(1);
      }
    });
  }

  @Override
  public void stop(Future<Void> stopFuture) throws Exception {
    finalizeVerticle(stopFuture);
  }

  private void initializeVerticle(Future<Void> startFuture) {
    try {
      JsonObject configuration = config().getJsonObject(REDIS_CONFIGURATION_KEY);
      RedisOptions options = new RedisOptions(configuration);
      redisClient = RedisClient.create(vertx, options);
      redisClient.get("NonExistingKey", initHandler -> {
        if (initHandler.succeeded()) {
          LOGGER.info("Initial connection check with Redis done");
        } else {
          startFuture.fail(initHandler.cause());
        }
      });
    } catch (Throwable throwable) {
      LOGGER.error("Not able to continue initialization.", throwable);
      startFuture.fail(throwable);
    }
  }

  private void finalizeVerticle(Future<Void> stopFuture) {
    if (redisClient != null) {
      redisClient.close(redisCloseAsyncHandler -> {
        if (redisCloseAsyncHandler.succeeded()) {
          LOGGER.info("Redis client has been closed successfully");
        } else {
          LOGGER.error("Error in closing redis client", redisCloseAsyncHandler.cause());
        }
        // Does not matter if we fail or succeed, We are closing down.
        stopFuture.complete();
      });
    }
  }

}
