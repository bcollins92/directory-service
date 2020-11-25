package com.bc92.directoryservice.service;

import java.util.Set;
import com.bc92.directoryservice.model.DirectoryNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation of a folder and its contents
 *
 * @author Brian
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadFolder {
  private String fullPath;
  private Set<String> childFolders;
  private Set<String> files;

  public ReadFolder(final DirectoryNode folderNode) {
    fullPath = folderNode.getFullPath();
    childFolders = folderNode.getChildren().keySet();
    files = folderNode.getFiles().keySet();
  }

}
