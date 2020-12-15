package com.bc92.directoryservice.service;

import com.bc92.directoryservice.model.Path;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class File {

  private String fullPath;
  private String parentPath;
  private String discriminator;
  private byte[] fileBytes;

  public File(final String parentPath, final String discriminator, final byte[] fileBytes) {
    this.discriminator = discriminator;
    this.parentPath = parentPath;
    this.fileBytes = fileBytes;
    this.validate();
  }

  public void validate() {
    Path.validatePath(parentPath);
    Path.validateDiscriminator(discriminator);
    fullPath = Path.combineParentPathAndDiscriminator(parentPath, discriminator);
    Path.validatePath(fullPath);
  }



}
