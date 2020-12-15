package com.bc92.directoryservice.model;

import org.springframework.util.Assert;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileNode {

  private String discriminator;

  private String fullPath;

  private String parentPath;

  @JsonIgnore
  private byte[] fileBytes;

  private String id;

  public FileNode(final String discriminator, final byte[] fileBytes) {
    this.discriminator = discriminator;
    this.fileBytes = fileBytes;
  }

  public FileNode(final DirElementDTO element, final DirectoryNode parent) {
    discriminator = element.getDiscriminator();
    parentPath = parent.getFullPath();
    fullPath = Path.combineParentPathAndDiscriminator(parentPath, discriminator);
    fileBytes = element.getFileBytes().array();
    id = element.getId();

    Assert.notNull(fileBytes, "filebytes must not be null");
    Assert.isTrue(element.getParentPath().equals(parent.getFullPath()),
        "Provided parent full path and element parent path must match for data integrity");
  }

  public void updateParentPath(final String newParentPath) {
    Path.validatePath(newParentPath);
    parentPath = newParentPath;
    fullPath = parentPath + DirectoryServiceConstants.PATH_DELIMINATOR + discriminator;
  }

}
