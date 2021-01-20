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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.bc92.directoryservice.service.File;
import com.bc92.directoryservice.service.FileService;
import com.bc92.directoryservice.service.ReadFile;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class FileController {

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);

  private final FileService fileService;


  @PostMapping(path = DirectoryServiceConstants.FILE_API_PATH, consumes = {"multipart/form-data"})
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void uploadFile(@RequestParam(value = "file") final MultipartFile file,
      @RequestParam final String parentPath, final Authentication auth) throws IOException {
    logger.trace(">><< uploadFile()");
    fileService.uploadFile(new File(parentPath, file.getOriginalFilename(), file.getBytes()),
        auth.getName());
  }

  @GetMapping(DirectoryServiceConstants.FILE_API_PATH)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Resource readFile(@RequestParam final String fullPath, final Authentication auth) {
    logger.trace(">><< readFile()");
    return fileService.readFile(fullPath, auth.getName());
  }

  @PutMapping(DirectoryServiceConstants.FILE_API_PATH)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ReadFile updateFile(final MultipartFile file, @RequestParam final String parentPath,
      final Authentication auth) throws IOException {
    logger.trace(">><< updateFolder()");
    return fileService.updateFile(new File(parentPath, file.getOriginalFilename(), file.getBytes()),
        auth.getName());
  }

  @DeleteMapping(DirectoryServiceConstants.FILE_API_PATH)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteFile(@RequestParam final String fullPath, final Authentication auth) {
    logger.trace(">><< deleteFolder()");
    fileService.deleteFile(fullPath, auth.getName());
  }

}
