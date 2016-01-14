package org.gooru.media.upload.routes;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import org.gooru.media.upload.constants.ConfigConstants;
import org.gooru.media.upload.constants.ErrorsConstants;
import org.gooru.media.upload.constants.HttpConstants.HttpStatus;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.gooru.media.upload.service.MediaUploadService;
import org.gooru.media.upload.service.MediaUploadServiceImpl;
import org.gooru.media.upload.service.S3Service;
import org.gooru.media.upload.utils.RouteResponseUtility;
import org.gooru.media.upload.utils.UploadValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteFileUploadConfigurator implements RouteConfigurator {

  private static final Logger LOG = LoggerFactory.getLogger("org.gooru.media.upload.bootstrap.FileUploadVerticle");

  private MediaUploadService uploadService;

  private S3Service s3Service;

  @Override
  public void configureRoutes(Vertx vertx, Router router, JsonObject config) {
    uploadService = new MediaUploadServiceImpl();
    final String s3ConfiFileLocation = config.getString(ConfigConstants.S3_CONFIG_FILE_LOCATION);
    final String uploadLocation = config.getString(ConfigConstants.UPLOAD_LOCATION);
    S3Service.setS3Config(s3ConfiFileLocation);
    s3Service = new S3Service();

    // upload file to file system
    router.post(RouteConstants.EP_FILE_UPLOAD).handler(context -> vertx.executeBlocking(future -> {
      String existingFname = context.request().getParam(RouteConstants.EXISTING_FILE_NAME);
      UploadResponse response = uploadService.uploadFile(context, uploadLocation, existingFname);
      if (!response.isHasError()) {
        response.setHttpStatus(HttpStatus.CREATED.getCode());
      }
      future.complete(response);
    }, res -> new RouteResponseUtility().responseHandler(context, res, LOG)));

    // move file to s3 
    router.put(RouteConstants.EP_FILE_UPLOAD_S3).handler(context -> vertx.executeBlocking(future -> {
      try {
        long start = System.currentTimeMillis();
        UploadResponse response = s3Service.uploadFileS3(context.getBodyAsJson(), uploadLocation);
        LOG.info("Elapsed time to complete upload file to s3 :" + (System.currentTimeMillis() - start) + " ms");
        if (!response.isHasError()) {
          response.setHttpStatus(HttpStatus.SUCCESS.getCode());
        }
        future.complete(response);
      } catch (Exception e) {
        context.fail(e);
      }
    }, res -> new RouteResponseUtility().responseHandler(context, res, LOG)));

    router.route().failureHandler(failureRoutingContext -> {
      int statusCode = failureRoutingContext.statusCode();
      if (statusCode == HttpStatus.TOO_LARGE.getCode()) {
        // If upload fails we need to delete file from the uploaded location
        for (FileUpload f : failureRoutingContext.fileUploads()) {
          String fileName = f.uploadedFileName();
          vertx.fileSystem().delete(fileName, result -> {
            if (result.failed()) {
              LOG.warn("Delete of file '{}' failed cause '{}' ", fileName, result.cause());
            } else {
              LOG.debug("Delete of file '{}' succeeded", fileName);
            }
          });
        }
        new RouteResponseUtility().errorResponseHandler(failureRoutingContext, LOG,
          UploadValidationUtils.rejectOnError(ErrorsConstants.FIELD_NA, ErrorsConstants.EC_VE_400, ErrorsConstants.VE_006), statusCode);
      } else {
        HttpServerResponse response = failureRoutingContext.response();
        if (statusCode != -1) {
          statusCode = HttpStatus.ERROR.getCode();
        }
        response.setStatusCode(statusCode);

        response.end();

      }
    });

  }

}
