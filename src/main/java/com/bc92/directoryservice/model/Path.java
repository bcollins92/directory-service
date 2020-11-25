package com.bc92.directoryservice.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;

/**
 * Class for iterating through path elements, to be used by Directory to search for nodes
 *
 * @author Brian
 *
 */
public class Path {
  private static final Logger logger = LoggerFactory.getLogger(Path.class);

  private final String primitivePath;
  private PathNode root;
  private PathNode curr;

  public Path(final String primitivePath) {
    this.primitivePath = primitivePath;
    validatePath(primitivePath);
    this.build(primitivePath);
  }

  /**
   * Build a iterable-style object from the provided path
   *
   * @param primitivePath
   */
  private void build(final String primitivePath) {
    String trimmedPath = primitivePath;
    if (primitivePath.charAt(0) == '/') {
      trimmedPath = primitivePath.substring(1);
    }
    String[] pathStringArr = trimmedPath.split("/");
    root = new PathNode(pathStringArr[0]);
    curr = root;

    for (int i = 1; i < pathStringArr.length; i++) {
      PathNode temp = new PathNode(pathStringArr[i]);
      curr.setChild(temp);
      curr = temp;
    }

    curr = root;
  }

  /**
   * Validate the provided path. Throws InvalidPathException if the provided path is null/empty
   * string or it contains invalid characters or format
   *
   * @param primitivePath - the provided path string
   */
  public static void validatePath(final String primitivePath) {
    logger.debug("Validating path: {}", primitivePath);
    if (primitivePath == null || primitivePath.isEmpty()) {
      throw new InvalidPathException(InvalidPathException.NULL_EMPTY_PATH);
    }

    if (!primitivePath.matches(DirectoryServiceConstants.FILE_PATH_REGEX)) {
      throw new InvalidPathException(InvalidPathException.INVALID_CHARS, primitivePath);
    }
  }

  /**
   * Validate the provided discriminator. Throws InvalidDiscriminatorException if the provided
   * discriminator is null/empty string or it contains invalid characters or format
   *
   * @param discriminator - the discriminator string
   */
  public static void validateDiscriminator(final String discriminator) {
    logger.debug("Validating discriminator: {}", discriminator);
    if (discriminator == null || discriminator.isEmpty()) {
      throw new InvalidDiscriminatorException(InvalidDiscriminatorException.NULL_EMPTY_PATH);
    }

    if (!discriminator.matches(DirectoryServiceConstants.DISCRIMINATOR_REGEX)) {
      throw new InvalidDiscriminatorException(InvalidDiscriminatorException.INVALID_CHARS,
          discriminator);
    }
  }

  /**
   * Checks if two provided paths are equal
   *
   * @param pathA - string of path
   * @param pathB - string of path
   * @return boolean
   */
  public static boolean pathsAreEqual(final String pathA, final String pathB) {
    if (pathA == null || pathB == null) {
      return false;
    }
    if (!(pathA.matches(DirectoryServiceConstants.FILE_PATH_REGEX)
        && pathB.matches(DirectoryServiceConstants.FILE_PATH_REGEX))) {
      return false;
    }

    String[] pathAarr = pathA.substring(1).split("/");
    String[] pathBarr = pathB.substring(1).split("/");

    if (pathAarr.length != pathBarr.length) {
      return false;
    }

    for (int i = 0; i < pathAarr.length; i++) {
      if (!(pathAarr[i].equals(pathBarr[i]))) {
        return false;
      }
    }

    return true;
  }

  public String getPrimitivePath() {
    return primitivePath;
  }

  public PathNode getRoot() {
    return root;
  }

  public PathNode getCurr() {
    return curr;
  }

  public boolean hasNext() {
    return curr.hasChild();
  }

  public PathNode next() {
    curr = curr.getChild();
    return curr;
  }

  public PathNode current() {
    return curr;
  }



}
