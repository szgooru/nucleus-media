package org.gooru.media.upload.service;

import java.io.File;
import java.util.Set;

import org.gooru.media.upload.constants.FileUploadConstants;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class MediaUploadServiceImpl implements MediaUploadService {

  private static final Logger LOG = LoggerFactory.getLogger(MediaUploadServiceImpl.class);
  
  @Override
  public UploadResponse uploadFile(RoutingContext context, String uploadLocation, S3Service s3Service) {
    Set<FileUpload> files = context.fileUploads();
    String entityType = context.request().getParam(RouteConstants.ENTITY_TYPE);
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Context uploaded files : " + files.size());
    }
    for (FileUpload f : files) {
      LOG.info("Orginal file name : " + f.fileName() + " Uploaded file name in file system : " + f.uploadedFileName());
     String fileName = renameFile(f.fileName(), f.uploadedFileName());
     return s3Service.uploadFileS3(uploadLocation, entityType, fileName);
    }
    return null;
  }

  private String renameFile(String originalFileName, String uploadedFileName) {
    try {
      // Get file extension 
      int index = originalFileName.lastIndexOf(FileUploadConstants.DOT);

      if (index > 0) {
        String exten = originalFileName.substring(index + 1);
        File oldFile = new File(uploadedFileName);
        File newFile = new File(uploadedFileName + FileUploadConstants.DOT + exten);
        oldFile.renameTo(newFile);
        uploadedFileName = newFile.getName();
        LOG.info("Renamed file name : " + uploadedFileName);
      }

    } catch (Exception e) {
      LOG.error("Rename file name failed : " + e);
    }
    return uploadedFileName;

  }
}
