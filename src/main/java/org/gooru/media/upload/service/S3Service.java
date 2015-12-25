package org.gooru.media.upload.service;

import java.io.File;

import org.gooru.media.upload.constants.ConfigConstants;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;

public class S3Service {
  
  private RestS3Service restS3Service;
  
  private AWSCredentials awsCredentials;
  
  private S3Bucket s3Bucket;
  
  public S3Service() {
    awsCredentials = new AWSCredentials(null, null);
    restS3Service = new RestS3Service(awsCredentials);
    s3Bucket = new S3Bucket(ConfigConstants.GOORU_BUCKET);
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

}
