package com.bc92.directoryservice.model;

import org.springframework.util.Assert;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
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

  private String id;

  public FileNode(final NodeDTO element, final DirectoryNode parent) {
    discriminator = element.getDiscriminator();
    parentPath = parent.getFullPath();
    fullPath = Path.combineParentPathAndDiscriminator(parentPath, discriminator);
    id = element.getId();

    Assert.isTrue(element.getParentPath().equals(parent.getFullPath()),
        "Provided parent full path and element parent path must match for data integrity");
  }

  public void updateParentPath(final String newParentPath) {
    Path.validatePath(newParentPath);
    parentPath = newParentPath;
    fullPath = parentPath + DirectoryServiceConstants.PATH_DELIMINATOR + discriminator;
  }

}
