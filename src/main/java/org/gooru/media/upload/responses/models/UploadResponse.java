package org.gooru.media.upload.responses.models;

import io.vertx.core.json.JsonObject;

public class UploadResponse extends JsonObject {

  private JsonObject response;

  private JsonObject error;

  private boolean hasError;

  private int httpStatus;

  public JsonObject getResponse() {
    return response;
  }

  public void setResponse(JsonObject response) {
    this.response = response;
  }

  public boolean isHasError() {
    return hasError;
  }

  public void setHasError(boolean hasError) {
    this.hasError = hasError;
  }

  public int getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(int httpStatus) {
    this.httpStatus = httpStatus;
  }

  public JsonObject getError() {
    return error;
  }

  public void setError(JsonObject error) {
    this.error = error;
  }

}
