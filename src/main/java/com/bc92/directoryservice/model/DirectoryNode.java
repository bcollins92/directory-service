package com.bc92.directoryservice.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;
import com.bc92.directoryservice.dto.DirElementDTO;
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

  @JsonIgnore
  private DirectoryNode parent;

  private Map<String, DirectoryNode> children = new HashMap<>();

  private Map<String, File> files = new HashMap<>();

  /**
   * Public constructor to be used for creating a child node, from a {@link #DirElementDTO}
   *
   * Will throw a IllegalArgumentException if there is data inconsistency between provided parent
   * node and DirElementDTO
   *
   * @param dirElementDto - Dto of the directory element
   * @param parent
   */
  public DirectoryNode(final DirElementDTO dirElementDto, final DirectoryNode parent) {
    this.parent = parent;
    discriminator = dirElementDto.getDiscriminator();
    parentPath = parent.getFullPath();
    fullPath = parentPath + DirectoryServiceConstants.PATH_DELIMINATOR + discriminator;

    Assert.isTrue(PathParser.pathsAreEqual(dirElementDto.getParentPath(), parent.getFullPath()),
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
    parentPath = parent == null ? "" : parent.getFullPath();
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
    parentPath = "";
  }

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
    file.setParentPath(fullPath);
    file.setFullPath(
        fullPath + DirectoryServiceConstants.PATH_DELIMINATOR + file.getDiscriminator());
    return files.put(file.getDiscriminator(), file);
  }

  public File removeFile(final String discriminator) {
    return files.remove(discriminator);
  }

  public String getDiscriminator() {
    return discriminator;
  }


  public Set<DirElementDTO> recurseFlatten(final Set<DirElementDTO> flattened, final String owner) {

    if (!DirectoryServiceConstants.ROOT_NODE_NAME.equals(discriminator)) {
      flattened.add(new DirElementDTO(owner, this));
    }

    children.forEach((disc, node) -> flattened.addAll(node.recurseFlatten(flattened, owner)));

    files.forEach((disc, file) -> flattened.add(new DirElementDTO(owner, file)));

    return flattened;
  }



}
