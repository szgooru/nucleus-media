package org.gooru.media.upload.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class MediaUploadServiceImpl implements MediaUploadService{

  static final Logger LOG = LoggerFactory.getLogger(MediaUploadServiceImpl.class);

  private S3Service s3Service;
  
  public  MediaUploadServiceImpl() {
    this.setS3Service(new S3Service());
  }
  
  @Override
  public String uploadFile(RoutingContext context, String existingFileName){
    Set<FileUpload> files = context.fileUploads();
    if(LOG.isDebugEnabled()){
      LOG.debug("Context uploaded files : " + files.size());
    }
    JsonObject json = new JsonObject();
    for (FileUpload f : files) {
      json.put("fileName", f.uploadedFileName());
      LOG.info("Orginal file name : "+ f.fileName() + " Uploaded file name in file system : " + f.uploadedFileName());
    }
    return json.toString();
  }

  @Override
  public String uploadFileS3(String sourceFilePath, String contentId){
    try {
      return getS3Service().uploadFileS3(sourceFilePath, contentId);
    } catch (Exception e) {
        LOG.error("Failed to upload file to s3 : ", e);
     }
    return null;
  }
  
  public S3Service getS3Service() {
    return s3Service;
  }

  public void setS3Service(S3Service s3Service) {
    this.s3Service = s3Service;
  }

}
