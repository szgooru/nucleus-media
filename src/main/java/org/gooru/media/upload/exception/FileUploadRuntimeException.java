package org.gooru.media.upload.exception;

public class FileUploadRuntimeException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 3022759830631765476L;


  private int statusCode;

  private String customErrorCode;

  public FileUploadRuntimeException(String message, int statusCode) {
    super();
    this.statusCode = statusCode;
  }

  public FileUploadRuntimeException(String message, String customErrorCode) {
    super();
    this.customErrorCode = customErrorCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getCustomErrorCode() {
    return customErrorCode;
  }

}
