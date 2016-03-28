package org.gooru.media.service;

import io.vertx.ext.web.RoutingContext;

import org.gooru.media.responses.models.UploadResponse;

public interface MediaUploadService {

  static MediaUploadService instance() {
    return new MediaUploadServiceImpl();
  }

  UploadResponse uploadFile(RoutingContext routingContext, String uploadLocation, long fileMaxSize);

}
