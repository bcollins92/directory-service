package com.bc92.directoryservice.model;

import com.bc92.projectsdk.constants.DirectoryServiceConstants;

public class InvalidPathException extends RuntimeException {

  private static final long serialVersionUID = 3663657920845556096L;

  public static final String NULL_EMPTY_PATH = "The path provided was null or empty";

  public static final String FOLDER_DOES_NOT_EXIST =
      "The folder \"%s\" provided did not exist in path \"%s\" ";

  public static final String FOLDER_ALREADY_EXISTS =
      "The folder \"%s\" provided already exists in path \"%s\", cannot be created";

  public static final String FILE_ALREADY_EXISTS =
      "The file \"%s\" provided did exist in directory path \"%s\", cannot be created";

  public static final String INVALID_CHARS =
      "The path \"%s\" either did not start with a slash \"/\", or contained invalid characters; it must match the regular expression: "
          + DirectoryServiceConstants.FILE_PATH_REGEX;

  public InvalidPathException(final String message, final Object... args) {
    super(String.format(message, args));
  }

  public InvalidPathException(final String message) {
    super(message);
  }
}
