package com.bc92.directoryservice.dto;

import java.nio.ByteBuffer;
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
public class DirElementDTO implements Comparable<DirElementDTO> {

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

  @Indexed(name = "fileBytes")
  private ByteBuffer fileBytes;

  @Indexed(name = "id")
  private String id;

  public DirElementDTO(final String owner, final FileNode file) {
    type = DirElementType.FILE;
    this.owner = owner;
    discriminator = file.getDiscriminator();
    fullPath = file.getFullPath();
    parentPath = file.getParentPath();
    fileBytes = ByteBuffer.wrap(file.getFileBytes());
    this.validate();
  }

  public DirElementDTO(final String owner, final File file) {
    type = DirElementType.FILE;
    this.owner = owner;
    discriminator = file.getDiscriminator();
    fullPath = file.getFullPath();
    parentPath = file.getParentPath();
    fileBytes = ByteBuffer.wrap(file.getFileBytes());
    this.validate();
  }

  public DirElementDTO(final String owner, final DirectoryNode node) {
    type = DirElementType.FOLDER;
    this.owner = owner;
    discriminator = node.getDiscriminator();
    fullPath = node.getFullPath();
    parentPath = node.getParentPath();
    id = node.getId();
    fileBytes = null;
    this.validate();
  }

  public DirElementDTO(final String owner, final Folder createFolder) {
    type = DirElementType.FOLDER;
    this.owner = owner;
    discriminator = createFolder.getDiscriminator();
    fullPath = createFolder.getFullPath();
    parentPath = createFolder.getParentPath();
    fileBytes = null;
    this.validate();
  }


  public DirElementDTO(final DirElementType type, final String owner, final String discriminator,
      final String fullPath, final String parentPath, final byte[] fileBytes) {
    this.type = type;
    this.owner = owner;
    this.discriminator = discriminator;
    this.fullPath = fullPath;
    this.parentPath = parentPath;
    this.fileBytes = fileBytes == null ? null : ByteBuffer.wrap(fileBytes);
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

    if (type == DirElementType.FILE) {
      Assert.notNull(fileBytes, "filebytes must not be null");
    }
  }


  /**
   * Compare length of path strings, shorter ones should turn up earlier in the sort. This is to
   * ensure that directories are dealt with before their child folders/files
   */
  @Override
  public int compareTo(final DirElementDTO element) {
    return fullPath.length() - element.getFullPath().length();
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof DirElementDTO)) {
      return false;
    }
    return fullPath.equals(((DirElementDTO) obj).getFullPath())
        && owner.equals(((DirElementDTO) obj).getOwner());
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
