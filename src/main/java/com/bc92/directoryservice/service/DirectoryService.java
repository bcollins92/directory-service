package com.bc92.directoryservice.service;

import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.dto.DirElementDTO;
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
      logger
          .error("Failed to expand directory for provided username, returning Http 500 to client");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
  }


  public ReadFolder createFolder(final Folder createFolder, final String username) {
    logger.trace(">> createFolder()");

    Directory dir = this.getUserDirectory(username);
    ReadFolder created = dir.createFolder(createFolder);
    directoryRepo.saveAll(dir.flatten());

    logger.trace("<< createFolder()");
    return created;
  }

  public ReadFolder readFolder(final String readFolder, final String username) {
    logger.trace(">> readFolder()");
    return this.getUserDirectory(username).readFolder(readFolder);
  }


  public Directory updateFolder(final UpdateFolder updateFolder, final String username) {
    logger.trace(">> updateFolder()");

    Directory dir = this.getUserDirectory(username);
    dir.updateFolderDiscriminator(updateFolder);
    directoryRepo.saveAll(dir.getSubDirectory(updateFolder.getNewFullPath()));

    logger.trace("<< updateFolder()");
    return dir;
  }


  public ReadFolder deleteFolder(final String deleteFolder, final String username) {
    logger.trace(">> deleteFolder()");

    Directory dir = this.getUserDirectory(username);
    Set<DirElementDTO> toBeDeleted = dir.getSubDirectory(deleteFolder);
    ReadFolder deletedFolder = dir.deleteParentAndAllChildren(deleteFolder);
    directoryRepo.deleteAll(toBeDeleted);

    logger.trace("<< deleteFolder()");
    return deletedFolder;
  }
}
