package com.bc92.directoryservice.service;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.repo.DirectoryRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DirectoryService {

  private static final Logger logger = LoggerFactory.getLogger(DirectoryService.class);

  private final DirectoryRepository directoryRepo;

  /**
   * Returns the directory of the provided username
   *
   * @param username - string of the username
   * @return Directory - the directory excluding fileBytes
   */
  public Directory getUserDirectory(final String username) {
    logger.trace(">><< getUserDirectory()");
    try {
      return Directory.expand(new HashSet<>(directoryRepo.findByOwner(username)), username);
    } catch (Exception e) {
      logger.error("Failed to expand directory for provided username", e);
      throw new DirectoryAccessException("Failed to expand directory for provided username");
    }
  }

  /**
   * Create the provided folder
   *
   * @param createFolder - DTO holding the folder information
   * @param username - name of the user who owns the file
   * @return ReadFolder - DTO containing file metadata
   */
  public ReadFolder createFolder(final Folder createFolder, final String username) {
    logger.trace(">> createFolder()");

    Directory dir = this.getUserDirectory(username);
    ReadFolder created = dir.createFolder(createFolder);
    directoryRepo.saveAll(dir.flatten());

    logger.trace("<< createFolder()");
    return created;
  }

  /**
   * Read the specified folder
   *
   * @param readFolder - string of the full path to folder to be read
   * @param username - name of the user who owns the file
   * @return ReadFolder - DTO containing file metadata
   */
  public ReadFolder readFolder(final String readFolder, final String username) {
    logger.trace(">> readFolder()");
    return this.getUserDirectory(username).readFolder(readFolder);
  }

  /**
   * Delete the specified folder
   *
   * @param deleteFolder - string of the full path to folder to be read
   * @param username - name of the user who owns the file
   * @return Directory - resulting directory object with folder deleted
   */
  public Directory deleteFolder(final String deleteFolder, final String username) {
    logger.trace(">> deleteFolder()");

    Directory dir = this.getUserDirectory(username);
    Set<NodeDTO> toBeDeleted = dir.getSubDirectory(deleteFolder);
    dir.deleteParentAndAllChildren(deleteFolder);
    directoryRepo.deleteAll(toBeDeleted);

    logger.trace("<< deleteFolder()");
    return dir;
  }

}
