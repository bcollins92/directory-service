package com.bc92.directoryservice.service;

import com.bc92.directoryservice.model.Path;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolder {

  private Folder target;
  private String newDiscriminator;

  public void validate() {
    target.validate();
    Path.validateDiscriminator(newDiscriminator);
  }

  public String getNewFullPath() {
    this.validate();
    if (newDiscriminator.charAt(0) == '/') {
      newDiscriminator = newDiscriminator.substring(1);
    }
    return target.getParentPath() + DirectoryServiceConstants.PATH_DELIMINATOR + newDiscriminator;
  }

}
