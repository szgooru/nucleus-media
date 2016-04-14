package org.gooru.media.constants;

/**
 * Constant definition that are used to read configuration
 */
public final class ConfigConstants {
    public static final String HTTP_PORT = "http.port";
    public static final String MAX_FILE_SIZE = "max.file.size.bytes";
    public static final String UPLOAD_LOCATION = "upload.location";
    public static final String S3_CONFIG_FILE_LOCATION = "s3.config.file.location";
    public static final String S3_CONFIG = "s3.config";
    public static final String S3_ACCESS_KEY = "s3.access.key";
    public static final String S3_SECRET = "s3.secret";
    public static final String S3_USER_BUCKET_NAME = "s3.user.bucket.name";
    public static final String S3_CONTENT_BUCKET_NAME = "s3.content.bucket.name";
    public static final String MBUS_TIMEOUT = "message.bus.send.timeout.milliseconds";
    public static final String MAX_REQ_BODY_SIZE = "request.body.size.max.mb";
    public static final String VERTICLES_DEPLOY_LIST = "verticles.deploy.list";
    public static final String PORT = "port";
    public static final String HOST = "host";
    public static final String REDIS = "redis.config";
    public static final String METRICS_PERIODICITY = "metrics.periodicity.seconds";

    private ConfigConstants() {
        throw new AssertionError();
    }
}
