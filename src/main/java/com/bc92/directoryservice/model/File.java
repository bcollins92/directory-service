package com.bc92.directoryservice.model;

import com.bc92.directoryservice.dto.DirElementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class File {

  private String discriminator;

  private String fullPath;

  private String parentPath;

  private byte[] fileBytes;

  public File(final DirElementDTO element, final DirectoryNode parent) {
    discriminator = element.getDiscriminator();
    fileBytes = element.getFileBytes();

    // TODO: check data integrity between parent and element
  }



}
