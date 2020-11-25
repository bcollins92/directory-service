package com.bc92.directoryservice.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DirectoryServiceException extends IllegalArgumentException {

  private static final long serialVersionUID = 6496444014608702413L;

  public static final String NO_ELEMENT_SPECIFIED =
      "Either discriminator or fullPath must be populated";

  public DirectoryServiceException(final String message) {
    super(message);
  }



}
