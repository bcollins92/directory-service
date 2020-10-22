package com.bc92.directoryservice.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.bc92.directoryservice.utilities.DirectoryServiceConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @formatter:off
/**
 * Node class for modelling the directory as a tree-like structure
 *
 * @author Brian
 *
 */ //@formatter:on
@Getter
@Setter
@NoArgsConstructor
public class DirectoryNode {
  private String discriminator;
  @JsonIgnore
  private DirectoryNode parent;
  private Map<String, DirectoryNode> children = new HashMap<>();
  private Map<String, File> files = new HashMap<>();

  public DirectoryNode(final String discriminator, final DirectoryNode parent) {
    this.discriminator = discriminator;
    this.parent = parent;
  }

  public DirectoryNode copy() {
    DirectoryNode copy = new DirectoryNode(discriminator, parent);
    copy.setChildren(children);
    copy.setFiles(files);
    return copy;
  }

  public DirectoryNode(final String discriminator) {
    this.discriminator = discriminator;
  }

  public DirectoryNode addChild(final String discr) {
    children.put(discr, new DirectoryNode(discr, this));
    return children.get(discr);
  }

  public DirectoryNode addChild(final DirectoryNode node) {
    children.put(node.getDiscriminator(), node);
    return children.get(node.getDiscriminator());
  }

  public void removeChild(final String discr) {
    children.remove(discr);
  }

  public boolean containsChild(final String discr) {
    return children.containsKey(discr);
  }

  public DirectoryNode getChild(final String discr) {
    return children.getOrDefault(discr, null);
  }

  public File getFile(final String displayName) {
    return files.getOrDefault(displayName, null);
  }

  public File addFile(final File file) {
    return files.put(file.getDisplayName(), file);
  }

  public File removeFile(final String displayName) {
    return files.remove(displayName);
  }

  public Set<String> recurseGetAllPaths(final Set<String> paths, final String rootPath) {

    String thisPath = rootPath + DirectoryServiceConstants.PATH_DELIMINATOR + discriminator;

    paths.add(thisPath);

    children.forEach((disc, node) -> paths.addAll(node.recurseGetAllPaths(paths, thisPath)));

    return paths;
  }


}
