package org.gooru.media.upload.service;

import io.vertx.ext.web.RoutingContext;
import org.gooru.media.upload.responses.models.UploadResponse;

public interface MediaUploadService {

  UploadResponse uploadFile(RoutingContext routingContext, String uploadLocation, String existingFname);

}
