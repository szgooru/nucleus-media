package org.gooru.media.upload.constants;

public class RouteConstants {

  // Helper constants
  public static final String API_VERSION = "v1";
  public static final String API_BASE_ROUTE = "/api/nucleus/" + API_VERSION + "/";

  // Helper: Entity name constants
  public static final String ENTITY_MEDIA = "media/upload";
  public static final String MOVE_FILE_S3 = "media/upload/s3";

  // Upload file = /api/nucleus/{version}/media/upload
  public static final String EP_FILE_UPLOAD = API_BASE_ROUTE + ENTITY_MEDIA;
  
  // Upload file = /api/nucleus/{version}/media/upload/s3
  public static final String EP_FILE_UPLOAD_S3 = API_BASE_ROUTE + MOVE_FILE_S3; 
  
  public static final String EXISTING_FILE_NAME = "existingFileName";
  public static final String CONTENT_ID = "contentId";
  public static final String FILE_ID = "fileId";


}
