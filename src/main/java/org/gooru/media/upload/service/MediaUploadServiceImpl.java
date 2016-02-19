package org.gooru.media.upload.service;

import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.net.URL;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.gooru.media.upload.constants.FileUploadConstants;
import org.gooru.media.upload.constants.HttpConstants.HttpStatus;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.exception.FileUploadRuntimeException;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.gooru.media.upload.utils.UploadValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaUploadServiceImpl implements MediaUploadService {

  private static final Logger LOG = LoggerFactory.getLogger(MediaUploadServiceImpl.class);

  @Override
  public UploadResponse uploadFile(RoutingContext context, String uploadLocation, long fileMaxSize) {
    UploadResponse response = new UploadResponse();
    String fileName = null;
    String entityType = context.request().getParam(RouteConstants.ENTITY_TYPE);
    String url = context.request().getParam(RouteConstants.URL);

    if (url != null && !url.isEmpty()) {
      response = UploadValidationUtils.validateFileUrl(url, response);
      if (response.isHasError()) {
        LOG.error("Upload by url failed");
        return response;
      } else {
        fileName = downloadAndSaveFile(url, uploadLocation, fileMaxSize);
        LOG.debug("File downloaded and saved.  Filename : " + fileName);
      }
    } else {
      Set<FileUpload> files = context.fileUploads();
      if (LOG.isDebugEnabled()) {
        LOG.debug("Context uploaded files : " + files.size());
      }

      for (FileUpload f : files) {
        LOG.info("Orginal file name : " + f.fileName() + " Uploaded file name in file system : " + f.uploadedFileName());
        fileName = renameFile(f.fileName(), f.uploadedFileName());
      }
    }
    if (fileName != null) {
      return S3Service.getInstance().uploadFileS3(uploadLocation, entityType, fileName, response);
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

  private static String downloadAndSaveFile(String fileUrl, String uploadLocation, Long fileMaxSize) {
    try {
      LOG.debug("File url : " + fileUrl);
      String extension = StringUtils.substringAfterLast(fileUrl, FileUploadConstants.DOT);
      String fileName = UUID.randomUUID().toString() + FileUploadConstants.DOT + extension;
      File outputFile = new File(uploadLocation + fileName);
      URL url = new URL(fileUrl);
      FileUtils.copyURLToFile(url, outputFile);

      if (outputFile.length() > fileMaxSize.intValue()) {
        outputFile.delete();
        throw new FileUploadRuntimeException("Url file upload failed, file size exceeded 5 MB ", HttpStatus.BAD_REQUEST.getCode());
      }

      return fileName;
    } catch (Exception e) {
      LOG.error("DownloadImage failed:exception:", e);
      throw new FileUploadRuntimeException("Download image failed", HttpStatus.ERROR.getCode());
    }
  }

}
