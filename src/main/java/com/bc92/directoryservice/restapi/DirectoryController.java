package com.bc92.directoryservice.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.repo.DirectoryRepository;
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

  private final DirectoryRepository directoryRepo;

  private final DirectoryService directoryService;

  /**
   * Gets the authenticating user's directory, returns only the root if directory has not been
   * populated
   *
   * @param auth - Authentication object of current user
   * @return Directory - the directory excluding fileBytes
   */
  @GetMapping(DirectoryServiceConstants.DIRECTORY_API_PATH)
  @ResponseBody
  public Directory getUserDirectory(final Authentication auth) {
    logger.trace(">><< getUserDirectory()");
    return directoryService.getUserDirectory(auth.getName());
  }

  // @GetMapping(DirectoryServiceConstants.DIRECTORY_API_PATH + "/test")
  // @ResponseBody
  // public Iterable<DirElementDTO> test(final Authentication auth) {
  //
  // return directoryRepo.saveAll(this.getElementSet());
  // }
  //
  //
  //
  // private Set<DirElementDTO> getElementSet() {
////@formatter:off
// DirElementDTO[] arr = {
//     new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1", "/root/folder1", "/root", null),
//     new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder2", "/root/folder2", "/root", null),
//     new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-1", "/root/folder1/folder1-1", "/root/folder1/", null),
//     new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-2", "/root/folder1/folder1-2", "/root/folder1/", null),
//     new DirElementDTO(DirElementType.FILE, "TestOwner", "myFile.jpg", "/root/folder2/myFile.jpg", "/root/folder2", new byte[] {1,2,3,4}),
//     new DirElementDTO(DirElementType.FILE, "TestOwner", "myText.txt", "/root/folder2/myText.txt", "/root/folder2", new byte[] {1,2,3,4})
// };
////@formatter:on
  //
  // return Arrays.stream(arr).collect(Collectors.toSet());
  // }

}


