package org.gooru.media.upload.service;

import io.vertx.ext.web.RoutingContext;

public interface MediaUploadService {

  String uploadFile(RoutingContext routingContext, String existingFileName);
  
  String uploadFileS3(String sourceFilePath, String contentId);
  
}
