package org.gooru.media.upload.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.gooru.media.upload.constants.S3Constants;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class S3Service {
  
  private RestS3Service restS3Service;
  
  private AWSCredentials awsCredentials;
  
  private S3Bucket s3Bucket;
  
  private static Properties props;
  
  static final Logger LOG = LoggerFactory.getLogger(S3Service.class);

  static{
    try {
      props = new Properties();
      FileInputStream is = new FileInputStream(new File(S3Constants.S3_ACCOUNT_FILE));
      props.load(is);
    } catch (Exception e) {
       LOG.error("Failed to load S3 config values ", e);
    }
  }
  
  public S3Service() {
    awsCredentials = new AWSCredentials(getS3Config(S3Constants.S3_ACCESS_KEY), getS3Config(S3Constants.S3_SECRET));
    restS3Service = new RestS3Service(awsCredentials);
    s3Bucket = new S3Bucket(getS3Config(S3Constants.S3_BUCKET_NAME));
  }
  
  public String uploadFileS3(String sourceFilePath, String contentId) throws Exception{
    File file = new File(sourceFilePath);
    String fileName = null;
    if (!file.isDirectory()) {
      S3Object fileObject = new S3Object(s3Bucket, file);
      fileObject = restS3Service.putObject(s3Bucket, fileObject);
      setPublicACL(fileObject);
    }
    // TODO : have to decide return signed url or actual file location. 
    return fileName;
  }
  
  private void setPublicACL(S3Object fileObject) throws Exception {
    AccessControlList objectAcl = restS3Service.getObjectAcl(s3Bucket, fileObject.getKey());
    objectAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_READ);
    fileObject.setAcl(objectAcl);
    restS3Service.putObject(s3Bucket, fileObject);
  }

  private static String getS3Config(String key){
    return props.getProperty(key);
  }
}
