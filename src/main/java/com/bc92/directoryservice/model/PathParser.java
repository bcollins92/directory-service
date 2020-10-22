package com.bc92.directoryservice.model;

import com.bc92.directoryservice.utilities.DirectoryServiceConstants;

/**
 * Class for iterating through path elements, to be used by Directory to search for nodes
 *
 * @author Brian
 *
 */
public class PathParser {
  private final String primitivePath;
  private PathNode root;
  private PathNode curr;

  public PathParser(final String primitivePath) {
    this.primitivePath = primitivePath;
    this.validatePath(primitivePath);
    this.build(primitivePath);
  }

  /*
   * Build a linked list representing the path string
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

  private void validatePath(final String primitivePath) {
    if (primitivePath == null || primitivePath.isEmpty()) {
      throw new InvalidPathException(InvalidPathException.NULL_EMPTY_PATH);
    }

    if (!primitivePath.matches(DirectoryServiceConstants.FILE_PATH_REGEX)) {
      throw new InvalidPathException(InvalidPathException.INVALID_CHARS, primitivePath);
    }
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
