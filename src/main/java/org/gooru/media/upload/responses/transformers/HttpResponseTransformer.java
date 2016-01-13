package org.gooru.media.upload.responses.transformers;

import java.util.Map;

import org.gooru.media.upload.responses.models.UploadError;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

class HttpResponseTransformer implements ResponseTransformer {

  static final Logger LOG = LoggerFactory.getLogger(ResponseTransformer.class);
  private UploadResponse message;
  private boolean transformed = false;
  private Map<String, String> headers;
  private int httpStatus;
  private String httpBody;
  
  private static final String ERROR_TYPE = "type";
  
  private static final String ERRORS = "errors";

  public HttpResponseTransformer(Object message) {
    this.message = (UploadResponse)message;
    if (message == null) {
      LOG.error("Invalid or null Message<Object> for initialization");
      throw new IllegalArgumentException("Invalid or null Message<Object> for initialization");
    }
    if (!(message instanceof JsonObject)) {
      LOG.error("Message body should be JsonObject");
      throw new IllegalArgumentException("Message body should be JsonObject");
    }
  }
  
  @Override
  public void transform() {
    if (!this.transformed) {
      processTransformation();
      this.transformed = true;
    }
  }

  @Override
  public String transformedBody() {
    transform();
    return this.httpBody;
  }

  @Override
  public Map<String, String> transformedHeaders() {
    transform();
    return this.headers;
  }

  @Override
  public int transformedStatus() {
    transform();
    return this.httpStatus;
  }

  private void processTransformation() {
    // First initialize the http status
    this.httpStatus = message.getHttpStatus();
    
    // Now delegate the body handling
    boolean hasError = message.isHasError();
    
    LOG.info("Request has error : " + hasError);
    
    if(hasError){
      processErrorTransformation(message.getError());
    }
    else {
      processSuccessTransformation(message.getResponse());
    }
    // Now that we are done, mark it as transformed
    this.transformed = true;
  }

  private void processErrorTransformation(UploadError messageBody) {
    JsonObject error = new JsonObject();
    error.put(ERROR_TYPE, messageBody.getType());
    error.put(ERRORS, messageBody.getErrors().toString());
    this.httpBody = error.toString();
  }

  private void processSuccessTransformation(JsonObject messageBody) {
    this.httpBody = messageBody.toString();
  }



}
