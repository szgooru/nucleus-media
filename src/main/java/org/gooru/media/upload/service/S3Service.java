package org.gooru.media.upload.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.gooru.media.upload.constants.FileUploadConstants;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.constants.S3Constants;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Context;

public class S3Service {
  
  private static Properties props;
  
  protected Context context;
  
  private RestS3Service restS3Service;
  
  private static AWSCredentials awsCredentials;
  
  static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
  
  public static void setS3Config(String fileLocation){
    try {
      props = new Properties();
      FileInputStream is = new FileInputStream(new File(fileLocation));
      props.load(is);
      LOG.info("S3 config values loaded");
    } catch (Exception e) {
       LOG.error("Failed to load S3 config values ", e);
    }
  }
  
  public S3Service() {
    awsCredentials = new AWSCredentials(getS3Config(S3Constants.S3_ACCESS_KEY), getS3Config(S3Constants.S3_SECRET));
    restS3Service = new RestS3Service(awsCredentials);
    LOG.info("accesskey : " + getS3Config(S3Constants.S3_ACCESS_KEY)  + " secret : " + getS3Config(S3Constants.S3_SECRET));
  }
  
  public void setPublicACL(String objectKey, String gooruBucket) throws Exception {
    S3Object fileObject = restS3Service.getObject(gooruBucket, objectKey);
    AccessControlList objectAcl = restS3Service.getObjectAcl(gooruBucket, fileObject.getKey());
    objectAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
    fileObject.setAcl(objectAcl);
    restS3Service.putObject(gooruBucket, fileObject);
  }

  public void uploadFileS3(String fileName , String uploadLocation, String contentId, String entityType) throws Exception{
    try{
      Path path = Paths.get(uploadLocation + fileName);
      byte[] data = Files.readAllBytes(path);
      String bucketName = getBucketName(entityType);
      if(bucketName != null){
        // Upload file to s3 
        S3Object fileObject = new S3Object(contentId + FileUploadConstants.UNDERSCORE + fileName, data);
        S3Object uploadedObject = restS3Service.putObject(bucketName, fileObject);
        if(uploadedObject != null){
          LOG.info("File uploaded to s3 succeeded :   key {} ", uploadedObject.getKey());
          setPublicACL(contentId + FileUploadConstants.UNDERSCORE + fileName, bucketName);
          LOG.info("Set public acl to uploaded file :   key {} ", uploadedObject.getKey());
          // Delete temp file after the s3 upload 
          boolean fileDeleted = Files.deleteIfExists(path);
          if(fileDeleted){
            LOG.info("Temp file have been deleted from local file system : File name {} ", path.getFileName());
          }
          else{
            LOG.error("File delete from local file system failed : File name {} ", path.getFileName());
          }
        }
      }
      else {
        throw new Exception("Entity type is invalid !");
      }
    }
    catch(Exception e){
      LOG.error("Upload failed : " +e);
      throw new Exception(e);
    }
  }
 
  private String getBucketName(String entityType){
    String bucketName = null;
    if(entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.CONTENT.name())){
      bucketName = getS3Config(S3Constants.S3_CONTENT_BUCKET_NAME);
    } 
    else if(entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.USER.name())){
      bucketName = getS3Config(S3Constants.S3_USER_BUCKET_NAME);
    }
    LOG.info("S3 upload bucket name {} ", bucketName);
    return bucketName;
  }
  
  private static String getS3Config(String key){
    return props.getProperty(key);
  }
}
