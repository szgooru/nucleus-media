package org.gooru.media.upload.utils;

import static org.gooru.media.upload.constants.ErrorsConstants.HTTP;
import static org.gooru.media.upload.constants.ErrorsConstants.MESSAGE;
import static org.gooru.media.upload.constants.ErrorsConstants.VE_002;
import static org.gooru.media.upload.constants.ErrorsConstants.VE_003;
import static org.gooru.media.upload.constants.ErrorsConstants.VE_004;
import static org.gooru.media.upload.constants.ErrorsConstants.VE_005;
import io.vertx.core.json.JsonObject;

import org.apache.commons.lang3.StringUtils;
import org.gooru.media.upload.constants.ErrorsConstants;
import org.gooru.media.upload.constants.FileUploadConstants;
import org.gooru.media.upload.constants.HttpConstants.HttpStatus;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.exception.FileUploadRuntimeException;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.jets3t.service.S3ServiceException;
import org.slf4j.Logger;

public class UploadValidationUtils {

  public static String rejectOnError(String fieldName, String message) {
    JsonObject error = new JsonObject();
    error.put(fieldName, message);
    return error.toString();
  }

  public static void rejectOnS3Error(Exception e, UploadResponse response, final Logger logger) {
    JsonObject error = new JsonObject();
    if (e instanceof S3ServiceException) {
      error.put(MESSAGE, e.getMessage());
      setResponse(error, response, HttpStatus.ERROR.getCode(), ErrorsConstants.UploadErrorType.SERVER.getType());
    } else {
      logger.error("S3 upload failed " + e);
      throw new FileUploadRuntimeException(e.getMessage(), ErrorsConstants.UploadErrorType.SERVER.getType());
    }
  }

  private static void setResponse(JsonObject error, UploadResponse response, int httpStatus, String errorType) {
    if (error != null && error.size() > 0) {
      response.setHasError(true);
      response.setError(error);
      response.setHttpStatus(httpStatus);
    }
  }

  public static UploadResponse validateEntityType(String entityType, UploadResponse response) {
    JsonObject error = new JsonObject();
    if (entityType == null || entityType.isEmpty()) {
      error.put(RouteConstants.ENTITY_TYPE, VE_004);
    }

    if (entityType != null) {
      if (!(entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.CONTENT.name()) || entityType
          .equalsIgnoreCase(RouteConstants.UploadEntityType.USER.name()))) {
        error.put(RouteConstants.ENTITY_TYPE, VE_005);
      }
    }
    setResponse(error, response, HttpStatus.BAD_REQUEST.getCode(), ErrorsConstants.UploadErrorType.VALIDATION.getType());
    return response;
  }

  public static UploadResponse validateFileUrl(String url, UploadResponse response) {
    JsonObject error = new JsonObject();
    if (!url.startsWith(HTTP)) {
      error.put(RouteConstants.URL, VE_002);
    }

    String extension = StringUtils.substringAfterLast(url, FileUploadConstants.DOT);
    if (extension == null || extension.isEmpty() || !isValidImgType(extension)) {
      error.put(RouteConstants.URL, VE_003);
    }

    setResponse(error, response, HttpStatus.BAD_REQUEST.getCode(), ErrorsConstants.UploadErrorType.VALIDATION.getType());
    return response;
  }

  private static boolean isValidImgType(String extension) {
    boolean valid = false;
    for (String type : FileUploadConstants.IMG_TYPES) {
      if (extension.equalsIgnoreCase(type)) {
        valid = true;
        break;
      }
    }
    return valid;
  }

}
