package org.gooru.media.upload.utils;

import org.gooru.media.upload.constants.ErrorsConstants;
import org.gooru.media.upload.constants.HttpConstants.HttpStatus;
import org.gooru.media.upload.constants.RouteConstants;
import org.gooru.media.upload.exception.FileUploadRuntimeException;
import org.gooru.media.upload.responses.models.Error;
import org.gooru.media.upload.responses.models.UploadError;
import org.gooru.media.upload.responses.models.UploadResponse;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.ServiceException;
import org.slf4j.Logger;

import io.vertx.core.json.JsonArray;

public class UploadValidationUtils extends ErrorsConstants {

  private static void addError(String fieldName, String errorCode, String message, JsonArray errors) {
    errors.add(new Error(fieldName, errorCode, message));
  }

  public static String rejectOnError(String fieldName, String errorCode, String message) {
    JsonArray errors = new JsonArray();
    errors.add(new Error(fieldName, errorCode, message));
    return errors.toString();
  }

  public static void rejectOnS3Error(Exception e, UploadResponse response, final Logger logger) {
    JsonArray errors = new JsonArray();
    if (e instanceof S3ServiceException) {
      addError(FIELD_NA, ((ServiceException) e).getErrorCode(), e.getMessage(), errors);
      setResponse(errors, response, HttpStatus.ERROR.getCode(), ErrorsConstants.UploadErrorType.SERVER.getType());
    } else {
      logger.error("S3 upload failed " + e);
      throw new FileUploadRuntimeException(e.getMessage(), ErrorsConstants.UploadErrorType.SERVER.getType());
    }
  }

  private static void setResponse(JsonArray errors, UploadResponse response, int httpStatus, String errorType) {
    if (errors.size() > 0) {
      response.setHasError(true);
      UploadError validationError = new UploadError();
      validationError.setErrors(errors);
      validationError.setType(errorType);
      response.setError(validationError);
      response.setHttpStatus(httpStatus);
    }
  }

  public UploadResponse validateEntityType(String entityType, UploadResponse response) {
    JsonArray errors = new JsonArray();
      if (entityType == null || entityType.isEmpty()) {
        addError(RouteConstants.ENTITY_TYPE, EC_VE_400, VE_004, errors);
      }

      if (entityType != null) {
        if (!(entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.CONTENT.name()) ||
          entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.USER.name()))) {
          addError(RouteConstants.ENTITY_TYPE, EC_VE_400, VE_005, errors);
        }
      }
    setResponse(errors, response, HttpStatus.BAD_REQUEST.getCode(), ErrorsConstants.UploadErrorType.VALIDATION.getType());
    return response;
  }

}
