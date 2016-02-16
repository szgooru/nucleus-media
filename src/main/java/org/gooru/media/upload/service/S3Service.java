package org.gooru.media.upload.service;

import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.constants.S3Constants;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.gooru.media.upload.utils.UploadValidationUtils;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class S3Service {

  private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
  private static final Logger S3_LOG = LoggerFactory.getLogger("log.s3");

  private static Properties props;
  protected Context context;
  private RestS3Service restS3Service;
  private static final String FILE_NAME = "filename";

  public S3Service() {
    try {
      AWSCredentials awsCredentials = new AWSCredentials(getS3Config(S3Constants.S3_ACCESS_KEY), getS3Config(S3Constants.S3_SECRET));
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
      LOG.debug("S3 config values loaded");
    } catch (Exception e) {
      LOG.error("Failed to load S3 config values ", e);
    }
  }

  private static String getS3Config(String key) {
    return props.getProperty(key);
  }

  public UploadResponse uploadFileS3(String fileLocation, String entityType, String fileName, UploadResponse response) {

    try {
      UploadValidationUtils.validateEntityType(entityType, response);
      if (response.isHasError()) {
        return response;
      }

      Path path = Paths.get(fileLocation + fileName);
      byte[] data = Files.readAllBytes(path);
      String bucketName = getBucketName(entityType);

      // Upload file to s3
      long start = System.currentTimeMillis();
      S3Object fileObject = new S3Object(fileName, data);
      S3Object uploadedObject = restS3Service.putObject(bucketName, fileObject);

      if (uploadedObject != null) {
        LOG.debug("File uploaded to s3 succeeded :   key {} ", uploadedObject.getKey());
        LOG.debug("Elapsed time to complete upload file to s3 in service :" + (System.currentTimeMillis() - start) + " ms");
        JsonObject res = new JsonObject();
        res.put(FILE_NAME, uploadedObject.getKey());
        S3_LOG.info("S3 Uploaded Id : " + uploadedObject.getKey());
        response.setResponse(res);
        // Delete temp file after the s3 upload
        boolean fileDeleted = Files.deleteIfExists(path);
        if (fileDeleted) {
          LOG.debug("Temp file have been deleted from local file system : File name {} ", path.getFileName());
        } else {
          LOG.error("File delete from local file system failed : File name {} ", path.getFileName());
        }
      }
    } catch (Exception e) {
      LOG.error("Upload failed : " + e);
      UploadValidationUtils.rejectOnS3Error(e, response, LOG);
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
    LOG.debug("S3 upload bucket name {} ", bucketName);
    return bucketName;
  }

  public static S3Service getInstance() {
    return Holder.INSTANCE;
  }

  private static class Holder {
    static final S3Service INSTANCE = new S3Service();
  }
}
