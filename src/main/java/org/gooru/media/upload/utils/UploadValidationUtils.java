package org.gooru.media.upload.utils;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
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

  public UploadResponse validateS3FileUpload(JsonObject requestValues, UploadResponse response) {
    JsonArray errors = new JsonArray();
    if (requestValues == null || requestValues.isEmpty()) {
      addError(FIELD_NA, EC_VE_400, VE_000, errors);
    } else {
      String fileName = requestValues.getString(RouteConstants.FILE_ID);
      String entityId = requestValues.getString(RouteConstants.ENTITY_ID);
      String entityType = requestValues.getString(RouteConstants.ENTITY_TYPE);

      if (fileName == null || fileName.isEmpty()) {
        addError(RouteConstants.FILE_ID, EC_VE_400, VE_002, errors);
      }

      if (entityId == null || entityId.isEmpty()) {
        addError(RouteConstants.ENTITY_ID, EC_VE_400, VE_003, errors);
      }

      if (entityType == null || entityType.isEmpty()) {
        addError(RouteConstants.ENTITY_TYPE, EC_VE_400, VE_004, errors);
      }

      if (entityType != null) {
        if (!(entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.CONTENT.name()) ||
          entityType.equalsIgnoreCase(RouteConstants.UploadEntityType.USER.name()))) {
          addError(RouteConstants.ENTITY_TYPE, EC_VE_400, VE_005, errors);
        }
      }
    }

    setResponse(errors, response, HttpStatus.BAD_REQUEST.getCode(), ErrorsConstants.UploadErrorType.VALIDATION.getType());
    return response;
  }

}
