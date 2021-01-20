package com.bc92.directoryservice.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.service.DirectoryService;
import com.bc92.directoryservice.service.Folder;
import com.bc92.directoryservice.service.ReadFolder;
import com.bc92.directoryservice.service.UpdateFolder;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class FolderController {

  private static final Logger logger = LoggerFactory.getLogger(FolderController.class);

  private final DirectoryService directoryService;


  @PostMapping(DirectoryServiceConstants.FOLDER_API_PATH)
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public ReadFolder createFolder(@RequestBody final Folder createFolder,
      final Authentication auth) {
    logger.trace(">><< createFolder()");
    return directoryService.createFolder(createFolder, auth.getName());
  }

  @GetMapping(DirectoryServiceConstants.FOLDER_API_PATH)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public ReadFolder readFolder(@RequestParam final String folder, final Authentication auth) {
    logger.trace(">><< readFolder()");
    return directoryService.readFolder(folder, auth.getName());
  }

  @PutMapping(DirectoryServiceConstants.FOLDER_API_PATH)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Directory updateFolder(@RequestBody final UpdateFolder folder, final Authentication auth) {
    logger.trace(">><< updateFolder()");
    return directoryService.updateFolder(folder, auth.getName());
  }

  @DeleteMapping(DirectoryServiceConstants.FOLDER_API_PATH)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteFolder(@RequestParam final String folder, final Authentication auth) {
    logger.trace(">><< deleteFolder()");
    directoryService.deleteFolder(folder, auth.getName());
  }

}
