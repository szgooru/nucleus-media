package org.gooru.media.upload.responses.models;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class UploadError extends JsonObject {

  private JsonArray errors;

  private String type;

  public JsonArray getErrors() {
    return errors;
  }

  public void setErrors(JsonArray errors) {
    this.errors = errors;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }


}
