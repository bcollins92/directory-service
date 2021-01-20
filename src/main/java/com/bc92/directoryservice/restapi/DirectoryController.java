package com.bc92.directoryservice.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.service.DirectoryService;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import lombok.AllArgsConstructor;

/**
 * Rest Controller responsible for managing the directory information such as its name, or
 * operations that might apply to the whole directory, such as deletion
 *
 * @author Brian
 *
 */
@RestController
@AllArgsConstructor
public class DirectoryController {

  private static final Logger logger = LoggerFactory.getLogger(DirectoryController.class);

  private final DirectoryService directoryService;

  /**
   * Gets the authenticating user's directory, returns only the root if directory has not been
   * populated
   *
   * @param auth - Authentication object of current user
   * @return Directory - the directory excluding fileBytes
   */
  @GetMapping(DirectoryServiceConstants.DIRECTORY_API_PATH)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public Directory getUserDirectory(final Authentication auth) {
    logger.trace(">><< getUserDirectory()");
    return directoryService.getUserDirectory(auth.getName());
  }

}


