package org.gooru.media.upload.bootstrap;


import org.gooru.media.upload.constants.ConfigConstants;
import org.gooru.media.upload.constants.HttpConstants;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.exception.FileUploadRuntimeException;
import org.gooru.media.upload.service.MediaUploadService;
import org.gooru.media.upload.service.MediaUploadServiceImpl;
import org.gooru.media.upload.service.S3Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class FileUploadVerticle extends AbstractVerticle {

  static final Logger LOG = LoggerFactory.getLogger(FileUploadVerticle.class);
 
  private  MediaUploadService uploadService;
  
  private S3Service s3Service;
  
  @Override
  public void start() throws Exception {
    
    LOG.info("Starting FileUploadVerticle...");
      
    final HttpServer httpServer = vertx.createHttpServer();
    
    final Router router = Router.router(vertx);
    
    // Configure the file upload handler  
    final long bodyLimit = config().getLong(ConfigConstants.MAX_FILE_SIZE);
    final String uploadLocation = config().getString(ConfigConstants.UPLOAD_LOCATION);
    uploadService = new MediaUploadServiceImpl();
    s3Service = new S3Service();
    
    BodyHandler bodyHandler = BodyHandler.create();
    bodyHandler.setBodyLimit(bodyLimit);
    bodyHandler.setUploadsDirectory(uploadLocation);
    router.route().handler(bodyHandler);
    
    router.route().failureHandler(failureRoutingContext -> {
      int statusCode = failureRoutingContext.statusCode();
      LOG.info( "Route failed : " + failureRoutingContext.request().absoluteURI() +  "  Cause : "+failureRoutingContext.failure().getMessage()  + " Status code: " + statusCode);
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
      response.setStatusCode(statusCode);
      response.end();
    });
   
    // upload file  
    router.post(RouteConstants.EP_FILE_UPLOAD).handler(context -> {
      String existingFname = context.request().getParam(RouteConstants.EXISTING_FILE_NAME);
      String response = uploadService.uploadFile(context, uploadLocation + existingFname);
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
      String sourceFilePath = context.request().getParam(RouteConstants.FILE_ID);
      String contentId = context.request().getParam(RouteConstants.CONTENT_ID);
      if(sourceFilePath != null && contentId != null){
        try{
          s3Service.uploadFileS3(sourceFilePath, contentId);
        }
        catch(Exception e){
          context.fail(e);
        }
      }
      else{
        context.fail(new FileUploadRuntimeException(HttpConstants.HttpStatus.BAD_REQUEST.getMessage(), HttpConstants.HttpStatus.BAD_REQUEST.getCode()));
      }
    });
    
    // If the port is not present in configuration then we end up
    // throwing as we are casting it to int. This is what we want.
    final int port = config().getInteger(ConfigConstants.HTTP_PORT);
    LOG.info("Http server starting on port {}", port);
    httpServer.requestHandler(router::accept).listen(port, result -> {
      if (result.succeeded()) {
        LOG.info("HTTP Server started successfully");
      } else {
        // Can't do much here, Need to Abort. However, trying to exit may have us blocked on other threads that we may have spawned, so we need to use
        // brute force here
        LOG.error("Not able to start HTTP Server", result.cause());
        Runtime.getRuntime().halt(1);
      }
    });
  
  }
  
}
