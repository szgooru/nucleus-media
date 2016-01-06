package org.gooru.media.upload.routes;

import org.gooru.media.upload.constants.ConfigConstants;
import org.gooru.media.upload.constants.HttpConstants;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.exception.FileUploadRuntimeException;
import org.gooru.media.upload.service.MediaUploadService;
import org.gooru.media.upload.service.MediaUploadServiceImpl;
import org.gooru.media.upload.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;

public class RouteFileUploadConfigurator implements RouteConfigurator {

  static final Logger LOG = LoggerFactory.getLogger("org.gooru.media.upload.bootstrap.FileUploadVerticle");

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
    router.post(RouteConstants.EP_FILE_UPLOAD).handler(context -> {
       String existingFname = context.request().getParam(RouteConstants.EXISTING_FILE_NAME);
       String response = uploadService.uploadFile(context, uploadLocation);
       if(existingFname != null && !existingFname.isEmpty()){
         vertx.fileSystem().delete(existingFname, result -> {
           if(result.failed()){
             LOG.warn("Delete of file '{}' failed cause '{}' ", existingFname, result.cause());         
           }
           else {
             LOG.debug("Delete of file '{}' succeeded", existingFname);         
           }
         });
       }
       LOG.info("Request URL : " + context.request().absoluteURI() + "  " + context.response().getStatusCode());
       context.response().end(response);
     });
    
    // move file to s3 
    router.put(RouteConstants.EP_FILE_UPLOAD_S3).handler(context -> {
      LOG.info("test log {} ", 1);
      LOG.info("test log 2 ");
      vertx.executeBlocking(future -> {
        String fileName = context.request().getParam(RouteConstants.FILE_ID);
        String contentId = context.request().getParam(RouteConstants.ENTITY_ID);
        String entityType = context.request().getParam(RouteConstants.ENTITY_TYPE);
        if(fileName != null && contentId != null && entityType != null){
          try{
            long start = System.currentTimeMillis();
            s3Service.uploadFileS3(fileName, uploadLocation, contentId, entityType);
            LOG.info("Elapsed time to complete upload file to s3 :" +(System.currentTimeMillis() - start) + " ms");
            future.complete();
          }
          catch(Exception e){
            context.fail(e);
          }
        }
        else{
          context.fail(new FileUploadRuntimeException(HttpConstants.HttpStatus.BAD_REQUEST.getMessage(), HttpConstants.HttpStatus.BAD_REQUEST.getCode()));
        }
      }, res ->   {
        System.out.println("Result " + res.succeeded());
        context.response().end();
      });
    });

    router.route().failureHandler(failureRoutingContext -> {
      int statusCode = failureRoutingContext.statusCode();
      LOG.error( "Route failed : " + failureRoutingContext.request().absoluteURI() +  "  Cause : "+failureRoutingContext.failure().getMessage()  + " Status code: " + statusCode);
      if(statusCode == HttpConstants.HttpStatus.TOO_LARGE.getCode()){
         // If upload fails we need to delete file from the uploaded location 
         for (FileUpload f : failureRoutingContext.fileUploads()) {
           String fileName = f.uploadedFileName();
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
      //TODO : have to add logic to send error message and code in json format 
      HttpServerResponse response = failureRoutingContext.response();
      if(statusCode != -1){
        response.setStatusCode(statusCode);
      }
      response.end();
    });

    
  }

}
