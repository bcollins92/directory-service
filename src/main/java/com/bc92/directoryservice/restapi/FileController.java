package com.bc92.directoryservice.restapi;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.service.DirectoryService;
import com.bc92.directoryservice.service.File;
import com.bc92.directoryservice.service.ReadFile;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  private final DirectoryService directoryService;


  @PostMapping(path = DirectoryServiceConstants.FILE_API_PATH, consumes = {"multipart/form-data"})
  @ResponseBody
  public ReadFile uploadFile(@RequestParam(value = "file") final MultipartFile file,
      @RequestParam final String parentPath, final Authentication auth) {
    logger.trace(">><< uploadFile()");
    try {
      return directoryService.uploadFile(
          new File(parentPath, file.getOriginalFilename(), file.getBytes()), auth.getName());
    } catch (IOException e) {
      logger.error("File upload encountered IO Error", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "File upload encountered IO Error");
    }
  }

  @GetMapping(DirectoryServiceConstants.FILE_API_PATH)
  @ResponseBody
  public Resource readFile(@RequestParam final String fullPath, final Authentication auth) {
    logger.trace(">><< readFile()");
    return directoryService.readFile(fullPath, auth.getName());
  }

  @PutMapping(DirectoryServiceConstants.FILE_API_PATH)
  @ResponseBody
  public ReadFile updateFile(final MultipartFile file, @RequestParam final String parentPath,
      final Authentication auth) {
    logger.trace(">><< updateFolder()");
    try {
      return directoryService.updateFile(
          new File(parentPath, file.getOriginalFilename(), file.getBytes()), auth.getName());
    } catch (IOException e) {
      logger.error("File update encountered IO Error", e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "File update encountered IO Error");
    }
  }

  @DeleteMapping(DirectoryServiceConstants.FILE_API_PATH)
  @ResponseBody
  public ReadFile deleteFile(@RequestParam final String fullPath, final Authentication auth) {
    logger.trace(">><< deleteFolder()");
    return directoryService.deleteFile(fullPath, auth.getName());
  }

}
