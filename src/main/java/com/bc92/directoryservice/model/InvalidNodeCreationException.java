package com.bc92.directoryservice.model;

public class InvalidNodeCreationException extends RuntimeException {

  private static final long serialVersionUID = 3663657920845556096L;

  public static final String ELEMENT_TYPE_UNKNOWN =
      "The Directory Element \"%s\" did not specify a type";

  public static final String ELEMENT_WRONG_TYPE =
      "The Directory Element \"%s\" specified the wrong type";

  public InvalidNodeCreationException(final String message, final Object... args) {
    super(String.format(message, args));
  }

  public InvalidNodeCreationException(final String message) {
    super(message);
  }
}
