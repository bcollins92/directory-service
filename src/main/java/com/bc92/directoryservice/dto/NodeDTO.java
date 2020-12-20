package com.bc92.directoryservice.dto;

import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.util.Assert;
import com.bc92.directoryservice.model.DirectoryNode;
import com.bc92.directoryservice.model.FileNode;
import com.bc92.directoryservice.model.Path;
import com.bc92.directoryservice.service.File;
import com.bc92.directoryservice.service.Folder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for persisting the directory tree nodes
 *
 * @author Brian
 *
 */
@Getter
@Setter
@NoArgsConstructor
@SolrDocument(collection = "test1")
public class NodeDTO implements Comparable<NodeDTO> {

  public enum DirElementType {
    FOLDER, FILE
  }

  @Indexed(name = "type")
  private DirElementType type;

  @Indexed(name = "owner")
  private String owner;

  @Indexed(name = "discriminator")
  private String discriminator;

  @Indexed(name = "fullPath")
  private String fullPath;

  @Indexed(name = "parentPath")
  private String parentPath;

  @Indexed(name = "id")
  private String id;

  public NodeDTO(final String owner, final FileNode file) {
    type = DirElementType.FILE;
    this.owner = owner;
    discriminator = file.getDiscriminator();
    fullPath = file.getFullPath();
    parentPath = file.getParentPath();
    this.validate();
  }

  public NodeDTO(final String owner, final File file) {
    type = DirElementType.FILE;
    this.owner = owner;
    discriminator = file.getDiscriminator();
    fullPath = file.getFullPath();
    parentPath = file.getParentPath();
    this.validate();
  }

  public NodeDTO(final String owner, final DirectoryNode node) {
    type = DirElementType.FOLDER;
    this.owner = owner;
    discriminator = node.getDiscriminator();
    fullPath = node.getFullPath();
    parentPath = node.getParentPath();
    id = node.getId();
    this.validate();
  }

  public NodeDTO(final String owner, final Folder createFolder) {
    type = DirElementType.FOLDER;
    this.owner = owner;
    discriminator = createFolder.getDiscriminator();
    fullPath = createFolder.getFullPath();
    parentPath = createFolder.getParentPath();
    this.validate();
  }


  public NodeDTO(final DirElementType type, final String owner, final String discriminator,
      final String fullPath, final String parentPath) {
    this.type = type;
    this.owner = owner;
    this.discriminator = discriminator;
    this.fullPath = fullPath;
    this.parentPath = parentPath;
    this.validate();
  }

  public void validate() {
    Assert.hasText(owner, "owner must not be null or empty");
    Assert.hasText(discriminator, "discriminator must not be null or empty");
    Assert.hasText(fullPath, "fullPath must not be null or empty");
    Assert.hasText(parentPath, "parentPath must not be null or empty");
    Path.validatePath(fullPath);
    Path.validatePath(parentPath);
    Path.validateDiscriminator(discriminator);
  }


  /**
   * Compare length of path strings, shorter ones should turn up earlier in the sort. This is to
   * ensure that directories are dealt with before their child folders/files
   */
  @Override
  public int compareTo(final NodeDTO element) {
    return fullPath.length() - element.getFullPath().length();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof NodeDTO)) {
      return false;
    }
    return fullPath.equals(((NodeDTO) obj).getFullPath())
        && owner.equals(((NodeDTO) obj).getOwner());
  }

  @Override
  public int hashCode() {
    return (owner + fullPath).hashCode();
  }

  @Override
  public String toString() {
    return "DirElementDTO [type=" + type + ", owner=" + owner + ", discriminator=" + discriminator
        + ", fullPath=" + fullPath + ", parentPath=" + parentPath + "]";
  }



}
