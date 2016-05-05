package org.gooru.media.service;

import org.gooru.media.responses.models.UploadResponse;

import io.vertx.ext.web.RoutingContext;

public interface MediaUploadService {

    static MediaUploadService instance() {
        return new MediaUploadServiceImpl();
    }

    UploadResponse uploadFile(RoutingContext routingContext, String uploadLocation, long fileMaxSize);

}
