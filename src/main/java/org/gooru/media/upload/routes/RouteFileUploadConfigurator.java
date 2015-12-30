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
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
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
    
    EventBus eb = vertx.eventBus();
    uploadService = new MediaUploadServiceImpl();
    final String s3ConfiFileLocation = config.getString(ConfigConstants.S3_CONFIG_FILE_LOCATION);
    final String s3Host = config.getString(ConfigConstants.S3_HOST);
    final String uploadLocation = config.getString(ConfigConstants.UPLOAD_LOCATION);
    S3Service.setS3Config(s3ConfiFileLocation);
    s3Service = new S3Service(createS3HttpClient(vertx, s3Host));

    
    // upload file to file system
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
              s3Service.uploadFileS3(uploadLocation + sourceFilePath, contentId);
          
        }
        catch(Exception e){
          context.fail(e);
        }
      }
      else{
        context.fail(new FileUploadRuntimeException(HttpConstants.HttpStatus.BAD_REQUEST.getMessage(), HttpConstants.HttpStatus.BAD_REQUEST.getCode()));
      }
      context.response().end();
    });

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

    
  }
  
  private final HttpClient createS3HttpClient(Vertx vertx, String host){
    return vertx.createHttpClient(new HttpClientOptions().setDefaultHost(host));
  }

}
