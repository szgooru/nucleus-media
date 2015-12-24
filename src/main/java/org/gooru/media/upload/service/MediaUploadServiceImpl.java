package org.gooru.media.upload.service;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class MediaUploadServiceImpl implements MediaUploadService{

  static final Logger LOG = LoggerFactory.getLogger(MediaUploadServiceImpl.class);

  @Override
  public String uploadFile(Vertx vertx, RoutingContext context, String existingFileName){
    Set<FileUpload> files = context.fileUploads();
    JsonObject json = new JsonObject();

    for (FileUpload f : files) {
      json.put("fileName", f.uploadedFileName());
    }
    if(existingFileName != null && !existingFileName.isEmpty()){
      deleteFile(vertx, existingFileName);
    }
    return json.toString();
  }

  @Override
  public void deleteFile(Vertx vertx, String fileName) {
    vertx.fileSystem().delete(fileName, result -> {
    if(result.failed()){
      LOG.warn("Delete of file '{}' failed cause '{}' ", fileName, result.cause());         
    }
    else {
      LOG.debug("Delete of file '{}' succeeded", fileName);         
    }
   });
  }

}
