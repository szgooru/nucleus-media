package org.gooru.media.upload.service;

import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public interface MediaUploadService {

  String uploadFile(Vertx vertx, RoutingContext routingContext, String existingFileName);
  
  void deleteFile(Vertx vertx, String fileName);
  
}
