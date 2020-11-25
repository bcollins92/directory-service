package com.bc92.directoryservice.model;

import com.bc92.projectsdk.constants.DirectoryServiceConstants;

public class InvalidDiscriminatorException extends RuntimeException {

  private static final long serialVersionUID = 8515507822364568805L;

  public static final String NULL_EMPTY_PATH = "The discriminator provided was null or empty";

  public static final String INVALID_CHARS =
      "The discriminator contained invalid characters; it must match the regular expression: "
          + DirectoryServiceConstants.DISCRIMINATOR_REGEX;

  public InvalidDiscriminatorException(final String message, final Object... args) {
    super(String.format(message, args));
  }

  public InvalidDiscriminatorException(final String message) {
    super(message);
  }

}
