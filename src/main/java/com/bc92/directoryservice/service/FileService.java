package com.bc92.directoryservice.service;

import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.dto.FileDTO;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.model.Path;
import com.bc92.directoryservice.repo.DirectoryRepository;
import com.bc92.directoryservice.repo.FileRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class FileService {

  private static final Logger logger = LoggerFactory.getLogger(FileService.class);

  private static final String FILE_NOT_FOUND = "File not found";

  private static final String PARENT_FOLDER_NOT_FOUND = "Parent folder not found";

  private static final String FILE_CONFLICT = "File already exists";

  private final DirectoryRepository directoryRepo;

  private final FileRepository fileRepo;


  /**
   * Get the specified folder from the repo
   *
   * @param fullPath - string representing the path of the folder
   * @param username - name of the user who owns the folder
   * @return NodeDTO - DTO containing the folder data
   */
  public NodeDTO getFolder(final String fullPath, final String username) {
    Path.validatePath(fullPath);
    return directoryRepo.findFolderByFullPathAndOwner(username, Path.escapeSlashes(fullPath));
  }

  /**
   * Get the specified file from the repo
   *
   * @param fullPath - string representing the path of the file, including filename
   * @param username - name of the user who owns the file
   * @return NodeDTO - DTO containing the file data
   */
  public FileDTO getFile(final String fullPath, final String username) {
    Path.validatePath(fullPath);
    return fileRepo.findFileByFullPathAndOwner(username, Path.escapeSlashes(fullPath));
  }

  /**
   * Return the bytes of a file, if it exists
   *
   * @param fullPath - string representing the path of the file, including filename
   * @param username - name of the user who owns the file
   * @return ByteArrayResource - file bytes
   */
  public Resource readFile(final String fullPath, final String username) {
    logger.trace(">> readFile()");
    FileDTO fileElement = this.getFile(fullPath, username);

    if (fileElement == null) {
      logger.error(FILE_NOT_FOUND);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FILE_NOT_FOUND);
    }

    logger.trace("<< readFile()");
    return new ByteArrayResource(fileElement.getFileBytes().array());
  }

  /**
   * Upload the provided file
   *
   * @param fullPath - string representing the path of the file, including filename
   * @param username - name of the user who owns the file
   * @return ReadFile - DTO containing file metadata
   */
  public ReadFile uploadFile(final File file, final String username) {
    logger.trace(">> uploadFile()");
    file.validate();

    if (this.getFile(file.getFullPath(), username) != null) {
      logger.error(FILE_CONFLICT);
      throw new ResponseStatusException(HttpStatus.CONFLICT, FILE_CONFLICT);
    }

    if (this.getFolder(file.getParentPath(), username) == null) {
      logger.error(PARENT_FOLDER_NOT_FOUND);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PARENT_FOLDER_NOT_FOUND);
    }

    fileRepo.save(new FileDTO(username, file));
    logger.trace("<< uploadFile()");
    return new ReadFile(file.getFullPath(), file.getDiscriminator());
  }

  /**
   * Delete the provided file
   *
   * @param fullPath - string representing the path of the file, including filename
   * @param username - name of the user who owns the file
   * @return ReadFile - DTO containing file metadata
   */
  public ReadFile deleteFile(final String fullPath, final String username) {
    logger.trace(">> deleteFile()");
    FileDTO fileElement = this.getFile(fullPath, username);

    if (fileElement == null) {
      logger.error(FILE_NOT_FOUND);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FILE_NOT_FOUND);
    }

    fileRepo.delete(fileElement);

    logger.trace("<< deleteFile()");
    return new ReadFile(fileElement.getParentPath(), fileElement.getDiscriminator());
  }

  /**
   * Update the provided file
   *
   * @param fullPath - string representing the path of the file, including filename
   * @param username - name of the user who owns the file
   * @return ReadFile - DTO containing file metadata
   */
  public ReadFile updateFile(final File file, final String username) {
    logger.trace(">> updateFile()");
    file.validate();
    FileDTO fileElement = this.getFile(file.getFullPath(), username);

    if (fileElement == null) {
      logger.error(FILE_NOT_FOUND);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, FILE_NOT_FOUND);
    }

    fileElement.setFileBytes(ByteBuffer.wrap(file.getFileBytes()));
    fileElement.validate();
    fileRepo.save(fileElement);

    logger.trace("<< updateFile()");
    return new ReadFile(file.getFullPath(), file.getDiscriminator());
  }

}
