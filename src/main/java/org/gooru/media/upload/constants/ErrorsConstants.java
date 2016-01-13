package org.gooru.media.upload.constants;

public class ErrorsConstants {
  
  public static final String VE_000 = "Request body should not be empty, pass valid json";
  
  public static final String VE_001 = "Sessiontoken cannot be null";
  
  public static final String VE_002 = "Filename cannot be null";
  
  public static final String VE_003 = "Entity id cannot be null";
  
  public static final String VE_004 = "Entity type cannot be null";
  
  public static final String VE_005 = "Invalid entity type";
  
  public static final String VE_006 = "File size should not be more than 5MB, upload file less than 5MB";
  
  public static final String EC_VE_400 = "VE400";
  
  public static final String FIELD_NA = "NA";
  
  public enum UploadErrorType {
    
    VALIDATION("validation"),
    SERVER("server");
    
    private String type;
    
    private UploadErrorType(String type){
      this.type = type;
    }
    
    public String getType(){
      return type;
    }
  }

  
}
