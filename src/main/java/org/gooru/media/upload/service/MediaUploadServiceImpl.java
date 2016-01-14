package org.gooru.media.upload.service;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;
import org.gooru.media.upload.constants.FileUploadConstants;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class MediaUploadServiceImpl implements MediaUploadService {

  static final Logger LOG = LoggerFactory.getLogger(MediaUploadServiceImpl.class);

  @Override
  public UploadResponse uploadFile(RoutingContext context, String uploadLocation, String existingFname) {
    UploadResponse response = new UploadResponse();
    Set<FileUpload> files = context.fileUploads();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Context uploaded files : " + files.size());
    }
    JsonObject json = new JsonObject();
    for (FileUpload f : files) {
      LOG.info("Orginal file name : " + f.fileName() + " Uploaded file name in file system : " + f.uploadedFileName());
      json.put(FileUploadConstants.FILE_NAME, renameFile(f.fileName(), f.uploadedFileName()));
    }
    response.setResponse(json);
    if (existingFname != null && !existingFname.isEmpty()) {
      deleteFile(uploadLocation + existingFname);
    }
    return response;
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

  private void deleteFile(String filePath) {
    Path path = Paths.get(filePath);
    try {
      Files.delete(path);
    } catch (Exception e) {
      LOG.error("Delete existing file failed " + e);
    }
  }

}
