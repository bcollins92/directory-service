package com.bc92.directoryservice.service;

import com.bc92.directoryservice.model.Path;
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
    return Path.combineParentPathAndDiscriminator(target.getParentPath(), newDiscriminator);
  }

}
