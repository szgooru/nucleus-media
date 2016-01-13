package org.gooru.media.upload.responses.models;

import io.vertx.core.json.JsonObject;

public class Error extends JsonObject {

  private static final String CODE = "code";
  
  private static final String FIELD_NAME = "field_name";
  
  private static final String MESSAGE = "message";
  
  public Error(String fieldName, String code, String message){
    this.setCode(code);
    this.setFieldName(fieldName);
    this.setMessage(message);
  }
  
  public String getCode() {
    return this.getString(CODE);
  }

  public void setCode(String code) {
    this.put(CODE, code);
  }

  public String getFieldName() {
    return this.getString(FIELD_NAME);
  }

  public void setFieldName(String fieldName) {
    this.put(FIELD_NAME, fieldName);
  }

  public String getMessage() {
    return this.getString(MESSAGE);
  }

  public void setMessage(String message) {
    this.put(MESSAGE, message);
  }
}
