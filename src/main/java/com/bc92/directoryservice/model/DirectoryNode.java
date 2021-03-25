package com.bc92.directoryservice.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

/**
 * Node class for modelling the directory as a tree-like structure
 *
 * @author Brian
 *
 */
@Getter
@Setter
public class DirectoryNode {

  private String discriminator;

  private String fullPath;

  private String parentPath;

  private String id;

  @JsonIgnore
  private DirectoryNode parent;

  private Map<String, DirectoryNode> children = new HashMap<>();

  private Map<String, FileNode> files = new HashMap<>();

  /**
   * Public constructor to be used for creating a child node, from a {@link #DirElementDTO}
   *
   * Will throw a IllegalArgumentException if there is data inconsistency between provided parent
   * node and DirElementDTO
   *
   * @param dirElementDto - Dto of the directory element
   * @param parent
   */
  public DirectoryNode(final NodeDTO dirElementDto, final DirectoryNode parent) {
    this.parent = parent;
    id = dirElementDto.getId();
    discriminator = dirElementDto.getDiscriminator();
    parentPath = parent.getFullPath();
    fullPath = parentPath + DirectoryServiceConstants.PATH_DELIMINATOR + discriminator;

    Assert.isTrue(Path.pathsAreEqual(dirElementDto.getParentPath(), parent.getFullPath()),
        "Provided parent full path and element parent path must match for data integrity");

  }

  /**
   * Private constructor to be used internally for creating a child node
   *
   * @param discriminator - the string representing the name of the node within the path
   * @param parent - the parent node
   */
  private DirectoryNode(final String discriminator, final DirectoryNode parent) {
    this.discriminator = discriminator;
    this.parent = parent;
    parentPath = parent == null ? DirectoryServiceConstants.ROOT_PARENT_PATH : parent.getFullPath();
    fullPath = parentPath + DirectoryServiceConstants.PATH_DELIMINATOR + discriminator;
  }

  /**
   * Public constructor to be used for creating the root node.
   *
   */
  public DirectoryNode() {
    discriminator = DirectoryServiceConstants.ROOT_NODE_NAME;
    fullPath =
        DirectoryServiceConstants.PATH_DELIMINATOR + DirectoryServiceConstants.ROOT_NODE_NAME;
    parentPath = DirectoryServiceConstants.ROOT_PARENT_PATH;
  }

  /**
   * Shallow copy this node
   *
   * @return - shallow copy of this directory node
   */
  public DirectoryNode copy() {
    DirectoryNode copy = new DirectoryNode(discriminator, parent);
    copy.setChildren(children);
    copy.setFiles(files);
    return copy;
  }

  public DirectoryNode addChild(final DirectoryNode node) {
    children.put(node.getDiscriminator(), node);
    return children.get(node.getDiscriminator());
  }

  public DirectoryNode removeChild(final String discr) {
    return children.remove(discr);
  }

  public boolean containsChild(final String discr) {
    return children.containsKey(discr);
  }

  public DirectoryNode getChild(final String discr) {
    return children.getOrDefault(discr, null);
  }

  public FileNode getFile(final String displayName) {
    return files.getOrDefault(displayName, null);
  }

  public FileNode addFile(final FileNode file) {
    file.setParentPath(fullPath);
    file.setFullPath(
        fullPath + DirectoryServiceConstants.PATH_DELIMINATOR + file.getDiscriminator());
    return files.put(file.getDiscriminator(), file);
  }

  public FileNode removeFile(final String discriminator) {
    return files.remove(discriminator);
  }

  public String getDiscriminator() {
    return discriminator;
  }

  public Set<NodeDTO> recurseFlatten(final Set<NodeDTO> flattened, final String owner) {

    if (!DirectoryServiceConstants.ROOT_NODE_NAME.equals(discriminator)) {
      flattened.add(new NodeDTO(owner, this));
    }

    children.forEach((disc, node) -> flattened.addAll(node.recurseFlatten(flattened, owner)));

    files.forEach((disc, file) -> flattened.add(new NodeDTO(owner, file)));

    return flattened;
  }



}
