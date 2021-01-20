package com.bc92.directoryservice.restapi;

import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.model.InvalidDiscriminatorException;
import com.bc92.directoryservice.model.InvalidNodeCreationException;
import com.bc92.directoryservice.model.InvalidPathException;

@ControllerAdvice
public class DirectoryControllerAdvice {

  private static final Logger logger = LoggerFactory.getLogger(DirectoryControllerAdvice.class);

  @ExceptionHandler({InvalidPathException.class, InvalidNodeCreationException.class,
      InvalidDiscriminatorException.class})
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMessage handleModelException(final Exception ex) {
    logger.debug("Handling exception from directory model", ex);
    return new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorMessage> handleResponseStatusException(
      final ResponseStatusException ex) {
    logger.error("This Exception was caught, returning {}", ex.getStatus());
    return new ResponseEntity<>(new ErrorMessage(ex.getStatus(), ex.getMessage()), ex.getStatus());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorMessage handleGenericException(final Exception ex) {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    logger.error("This Exception was caught, returning 500 at {}", timestamp, ex);
    return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "An internal server error occured",
        timestamp);
  }


}
