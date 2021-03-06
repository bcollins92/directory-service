package com.bc92.directoryservice.service;

import com.bc92.directoryservice.model.Path;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

  private String discriminator;
  private String parentPath;

  @Getter
  @Setter(AccessLevel.NONE)
  private String fullPath;

  public Folder(final String discriminator, final String parentPath) {
    this.discriminator = discriminator;
    this.parentPath = parentPath;
  }


  /**
   * Builds and validates the full path from provided data, throws DirectoryServiceException if
   * there is
   */
  public void validate() {

    Path.validatePath(parentPath);
    Path.validateDiscriminator(discriminator);
    fullPath = Path.combineParentPathAndDiscriminator(parentPath, discriminator);
    Path.validatePath(fullPath);
  }



}
