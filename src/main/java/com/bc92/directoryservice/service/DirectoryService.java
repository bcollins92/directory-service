package com.bc92.directoryservice.service;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.model.Path;
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

  public DirElementDTO getFile(final String fullPath, final String username) {
    Path.validatePath(fullPath);
    return directoryRepo.findFileByFullPathAndOwner(username, Path.escapeBackslashes(fullPath));
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

  public Resource readFile(final String fullPath, final String username) {
    DirElementDTO fileElement = this.getFile(fullPath, username);

    if (fileElement == null) {
      logger.error("File not found");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
    }

    return new ByteArrayResource(fileElement.getFileBytes().array());
  }

  public ReadFile uploadFile(final File file, final String username) {
    file.validate();
    if (this.getFile(file.getFullPath(), username) != null) {
      logger.error("File already exists");
      throw new ResponseStatusException(HttpStatus.CONFLICT, "File already exists");
    }

    Directory dir = this.getUserDirectory(username);
    dir.addDirectoryElement(new DirElementDTO(username, file));
    directoryRepo.saveAll(dir.flatten());

    return new ReadFile(file.getFullPath(), file.getDiscriminator());
  }

  public ReadFile deleteFile(final String fullPath, final String username) {
    DirElementDTO fileElement = this.getFile(fullPath, username);

    if (fileElement == null) {
      logger.error("File not found");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
    }

    directoryRepo.delete(fileElement);

    return new ReadFile(fileElement.getParentPath(), fileElement.getDiscriminator());
  }

  public ReadFile updateFile(final File file, final String username) {
    file.validate();
    DirElementDTO fileElement = this.getFile(file.getFullPath(), username);

    if (fileElement == null) {
      logger.error("File not found");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File not found");
    }

    fileElement.setFileBytes(ByteBuffer.wrap(file.getFileBytes()));
    fileElement.validate();
    directoryRepo.save(fileElement);

    return new ReadFile(file.getFullPath(), file.getDiscriminator());
  }



}
