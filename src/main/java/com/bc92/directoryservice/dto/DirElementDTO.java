package com.bc92.directoryservice.dto;

import java.util.Arrays;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.util.Assert;
import com.bc92.directoryservice.model.DirectoryNode;
import com.bc92.directoryservice.model.File;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for persisting the directory tree nodes
 *
 * @author Brian
 *
 */
@Getter
@Setter
@AllArgsConstructor
@SolrDocument(collection = "test1")
public class DirElementDTO implements Comparable<DirElementDTO> {

  public enum DirElementType {
    FOLDER, FILE
  }

  @Indexed(name = "type")
  private final DirElementType type;

  @Indexed(name = "owner")
  private final String owner;

  @Indexed(name = "discriminator")
  private final String discriminator;

  @Id
  @Indexed(name = "fullPath")
  private final String fullPath;

  @Indexed(name = "parentPath")
  private final String parentPath;

  @Indexed(name = "fileBytes")
  private final byte[] fileBytes;

  public DirElementDTO(final String owner, final File file) {
    type = DirElementType.FILE;
    this.owner = owner;
    discriminator = file.getDiscriminator();
    fullPath = file.getFullPath();
    parentPath = file.getParentPath();
    fileBytes = file.getFileBytes();
    Assert.notNull(fileBytes, "filebytes must not be null");
    this.validate();
  }

  public DirElementDTO(final String owner, final DirectoryNode node) {
    type = DirElementType.FOLDER;
    this.owner = owner;
    discriminator = node.getDiscriminator();
    fullPath = node.getFullPath();
    parentPath = node.getParentPath();
    fileBytes = null;
    this.validate();
  }

  private void validate() {
    Assert.hasText(owner, "owner must not be null or empty");
    Assert.hasText(discriminator, "discriminator must not be null or empty");
    Assert.hasText(fullPath, "fullPath must not be null or empty");
    Assert.hasText(parentPath, "parentPath must not be null or empty");
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
    return fullPath.equals(((DirElementDTO) obj).getFullPath());
  }

  @Override
  public int hashCode() {
    return fullPath.hashCode();
  }

  @Override
  public String toString() {
    return "DirElementDTO [type=" + type + ", owner=" + owner + ", discriminator=" + discriminator
        + ", fullPath=" + fullPath + ", parentPath=" + parentPath + ", fileBytes="
        + Arrays.toString(fileBytes) + "]";
  }



}
