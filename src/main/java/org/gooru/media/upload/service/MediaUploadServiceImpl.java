package org.gooru.media.upload.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class MediaUploadServiceImpl implements MediaUploadService{

  static final Logger LOG = LoggerFactory.getLogger(MediaUploadServiceImpl.class);
  
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
}
