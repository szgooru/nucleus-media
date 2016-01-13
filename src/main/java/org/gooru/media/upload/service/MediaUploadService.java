package org.gooru.media.upload.service;

import org.gooru.media.upload.responses.models.UploadResponse;

import io.vertx.ext.web.RoutingContext;

public interface MediaUploadService {

  UploadResponse uploadFile(RoutingContext routingContext, String uploadLocation, String existingFname);
  
}
