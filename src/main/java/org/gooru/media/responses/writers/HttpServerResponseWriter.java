package org.gooru.media.responses.writers;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.gooru.media.constants.HttpConstants;
import org.gooru.media.responses.transformers.ResponseTransformer;
import org.gooru.media.responses.transformers.ResponseTransformerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

class HttpServerResponseWriter implements ResponseWriter {

    static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);
    private final RoutingContext routingContext;
    private final AsyncResult<Object> message;

    public HttpServerResponseWriter(RoutingContext routingContext, AsyncResult<Object> message) {
        this.routingContext = routingContext;
        this.message = message;
    }

    @Override
    public void writeResponse() {
        ResponseTransformer transformer = ResponseTransformerBuilder.build(message.result());
        final HttpServerResponse response = routingContext.response();
        // First set the status code
        response.setStatusCode(transformer.transformedStatus());
        // Then set the headers
        Map<String, String> headers = transformer.transformedHeaders();
        if (headers != null && !headers.isEmpty()) {
            // Never accept content-length from others, we do that
            headers.entrySet().stream().filter(
                stringStringEntry -> !stringStringEntry.getKey().equalsIgnoreCase(HttpConstants.HEADER_CONTENT_LENGTH))
                .forEach(stringStringEntry -> {
                    response.putHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                });
        }
        // Then it is turn of the body to be set and ending the response
        final String responseBody =
            ((transformer.transformedBody() != null) && (!transformer.transformedBody().isEmpty())) ?
                transformer.transformedBody() : null;
        if (responseBody != null) {
            response.putHeader(HttpConstants.HEADER_CONTENT_LENGTH,
                Integer.toString(responseBody.getBytes(StandardCharsets.UTF_8).length));
            response.end(responseBody);
        } else {
            response.end();
        }
    }
}
