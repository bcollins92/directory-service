package com.bc92.directoryservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * class for modelling a path, such as /root/folder1/folder2
 *
 * @author Brian
 *
 */
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class PathNode {
  @NonNull
  String discriminator;
  PathNode parent;
  PathNode child;

  public boolean hasChild() {
    return child != null;
  }
}
