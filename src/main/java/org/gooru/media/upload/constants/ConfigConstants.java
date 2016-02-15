package org.gooru.media.upload.constants;

/**
 * Constant definition that are used to read configuration
 */
public final class ConfigConstants {

  public static final String HTTP_PORT = "http.port";

  public static final String MAX_FILE_SIZE = "max.file.size.bytes";

  public static final String UPLOAD_LOCATION = "upload.location";

  public static final String S3_CONFIG_FILE_LOCATION = "s3.config.file.location";

  public static final String S3_HOST = "s3.host";

  public static final String MBUS_TIMEOUT = "message.bus.send.timout.seconds";

  private ConfigConstants() {
  }
}
