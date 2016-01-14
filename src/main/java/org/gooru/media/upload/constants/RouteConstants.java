package org.gooru.media.upload.constants;

public class RouteConstants {

  // Helper constants
  public static final String API_VERSION = "v1";
  public static final String API_BASE_ROUTE = "/api/nucleus/" + API_VERSION + "/";

  // Helper: Entity name constants
  public static final String ENTITY_MEDIA = "media/upload";
  public static final String MOVE_FILE_S3 = "media/upload/s3";

  public static final String API_AUTH_ROUTE = "/api/nucleus/*";

  // Upload file = /api/nucleus/{version}/media/upload
  public static final String EP_FILE_UPLOAD = API_BASE_ROUTE + ENTITY_MEDIA;

  // Upload file = /api/nucleus/{version}/media/upload/s3
  public static final String EP_FILE_UPLOAD_S3 = API_BASE_ROUTE + MOVE_FILE_S3;

  public static final String EXISTING_FILE_NAME = "existingFileName";
  public static final String ENTITY_ID = "entityId";
  public static final String ENTITY_TYPE = "entityType";
  public static final String FILE_ID = "fileId";

  public enum UploadEntityType {
    CONTENT("content"),
    USER("user");

    private String message;

    private UploadEntityType(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }


}
