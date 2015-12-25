package org.gooru.media.upload.exception;

public class FileUploadRuntimeException extends RuntimeException{

  /**
   * 
   */
  private static final long serialVersionUID = 3022759830631765476L;
  
  
  private int statusCode;
  
  public FileUploadRuntimeException(String message, int statusCode){
    super();
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

}
