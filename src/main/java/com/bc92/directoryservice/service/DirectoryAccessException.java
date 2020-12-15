package com.bc92.directoryservice.service;

public class DirectoryAccessException extends RuntimeException {

  public DirectoryAccessException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public DirectoryAccessException(final String message) {
    super(message);
  }


}
