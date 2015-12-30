package org.gooru.media.upload.service;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.gooru.media.upload.constants.S3Constants;
import org.gooru.media.upload.s3client.S3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Context;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;

public class S3Service {
  
  private S3Client s3Client;
  
  private static Properties props;
  
  protected Context context;
  
  static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
  
  public static void setS3Config(String fileLocation){
    try {
      props = new Properties();
      FileInputStream is = new FileInputStream(new File(fileLocation));
      props.load(is);
      LOG.info("S3 config values loaded");
    } catch (Exception e) {
       LOG.error("Failed to load S3 config values ", e);
    }
  }
  
  public S3Service(HttpClient client) {
    LOG.info("accesskey : " + getS3Config(S3Constants.S3_ACCESS_KEY)  + " secret : " + getS3Config(S3Constants.S3_SECRET) + " bucket name : " + getS3Config(S3Constants.S3_BUCKET_NAME));
    this.s3Client = new S3Client(getS3Config(S3Constants.S3_ACCESS_KEY), getS3Config(S3Constants.S3_SECRET), client); 
  }
  
  public void uploadFileS3(String sourceFilePath, String contentId) throws Exception{
    Path path = Paths.get(sourceFilePath);
    byte[] data = Files.readAllBytes(path);
    Buffer buffer = Buffer.buffer(data);
    
    LOG.info("File size : " + data.length);
    
/*    s3Client.get(getS3Config(S3Constants.S3_BUCKET_NAME), "tfa-gsw-logo.png", response -> {
      LOG.info("Response Code : " + response.statusCode());
      LOG.info("Response message : " + response.statusMessage());
    });
*/    
    s3Client.put(getS3Config(S3Constants.S3_BUCKET_NAME), contentId, buffer, response -> {
      LOG.info("Response Code : " + response.statusCode());
      LOG.info("Response message : " + response.statusMessage());
      
    });
  }
 
  private static String getS3Config(String key){
    return props.getProperty(key);
  }
}
