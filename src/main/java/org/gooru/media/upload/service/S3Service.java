package org.gooru.media.upload.service;

import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;
import org.gooru.media.upload.constants.FileUploadConstants;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.constants.S3Constants;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.gooru.media.upload.utils.UploadValidationUtils;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class S3Service extends UploadValidationUtils {

  static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
  private static Properties props;
  private static AWSCredentials awsCredentials;
  protected Context context;
  private RestS3Service restS3Service;

  public S3Service() {
    try {
      awsCredentials = new AWSCredentials(getS3Config(S3Constants.S3_ACCESS_KEY), getS3Config(S3Constants.S3_SECRET));
      restS3Service = new RestS3Service(awsCredentials);
    } catch (Exception e) {
      LOG.error("S3 rest service start failed ! ", e);
    }
  }

  public static void setS3Config(String fileLocation) {
    try {
      props = new Properties();
      FileInputStream is = new FileInputStream(new File(fileLocation));
      props.load(is);
      LOG.info("S3 config values loaded");
    } catch (Exception e) {
      LOG.error("Failed to load S3 config values ", e);
    }
  }

  private static String getS3Config(String key) {
    return props.getProperty(key);
  }

  public UploadResponse uploadFileS3(JsonObject requestParams, String uploadLocation) throws Exception {
    UploadResponse response = new UploadResponse();
    try {
      validateS3FileUpload(requestParams, response);
      if (response.isHasError()) {
        return response;
      }

      String fileName = requestParams.getString(RouteConstants.FILE_ID);
      String entityId = requestParams.getString(RouteConstants.ENTITY_ID);
      String entityType = requestParams.getString(RouteConstants.ENTITY_TYPE);

      Path path = Paths.get(uploadLocation + fileName);
      byte[] data = Files.readAllBytes(path);
      String bucketName = getBucketName(entityType);

      // Upload file to s3
      long start = System.currentTimeMillis();
      S3Object fileObject = new S3Object(entityId + FileUploadConstants.UNDERSCORE + fileName, data);
      S3Object uploadedObject = restS3Service.putObject(bucketName, fileObject);

      if (uploadedObject != null) {
        LOG.info("File uploaded to s3 succeeded :   key {} ", uploadedObject.getKey());
        LOG.info("Elapsed time to complete upload file to s3 in service :" + (System.currentTimeMillis() - start) + " ms");
        JsonObject res = new JsonObject();
        res.put("fileName", uploadedObject.getKey());
        response.setResponse(res);
        // Delete temp file after the s3 upload
        boolean fileDeleted = Files.deleteIfExists(path);
        if (fileDeleted) {
          LOG.info("Temp file have been deleted from local file system : File name {} ", path.getFileName());
        } else {
          LOG.error("File delete from local file system failed : File name {} ", path.getFileName());
        }
      }
    } catch (Exception e) {
      LOG.error("Upload failed : " + e);
      rejectOnS3Error(e, response, LOG);
      return response;
    }
    return response;
  }

  private String getBucketName(String entityType) {
    String bucketName = null;
    if (entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.CONTENT.name())) {
      bucketName = getS3Config(S3Constants.S3_CONTENT_BUCKET_NAME);
    } else if (entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.USER.name())) {
      bucketName = getS3Config(S3Constants.S3_USER_BUCKET_NAME);
    }
    LOG.info("S3 upload bucket name {} ", bucketName);
    return bucketName;
  }
}
