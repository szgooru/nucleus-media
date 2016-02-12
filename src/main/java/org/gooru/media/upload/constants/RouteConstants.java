package org.gooru.media.upload.constants;

public class RouteConstants {

  // Helper constants
  private static final String API_VERSION = "v1";
  private static final String API_BASE_ROUTE = "/api/nucleus-media/" + API_VERSION + "/";

  // Helper: Entity name constants
  private static final String UPLOADS = "uploads";

  public static final String API_AUTH_ROUTE = "/api/nucleus-media/*";

  // Upload file = /api/nucleus-media/{version}/uploads
  public static final String EP_FILE_UPLOAD_S3 = API_BASE_ROUTE + UPLOADS;

  public static final String ENTITY_TYPE = "entity_type";

  public enum UploadEntityType {
    CONTENT("content"),
    USER("user");

    private final String message;

    UploadEntityType(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }


}
