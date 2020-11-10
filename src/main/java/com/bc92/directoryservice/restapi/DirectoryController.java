package com.bc92.directoryservice.restapi;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.dto.DirElementDTO.DirElementType;
import com.bc92.directoryservice.repo.DirectoryRepository;
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

  @PostMapping(DirectoryServiceConstants.DIRECTORY_API_PATH)
  public Iterable<DirElementDTO> createCurrentUsersFolder() {


    return directoryRepo.saveAll(this.getElementSet());
  }



  private Set<DirElementDTO> getElementSet() {
 // @formatter:off
    DirElementDTO[] arr = {
        new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1", "root/folder1", "root", new byte[] {}),
        new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder2", "root/folder2", "root", new byte[] {}),
        new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-1", "root/folder1/folder1-1", "root/folder1/", new byte[] {}),
        new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-2", "root/folder1/folder1-2", "root/folder1/", new byte[] {}),
        new DirElementDTO(DirElementType.FILE, "TestOwner", "myFile.jpg", "root/folder2/myFile.jpg", "root/folder2", new byte[] {}),
        new DirElementDTO(DirElementType.FILE, "TestOwner", "myText.txt", "root/folder2/myText.txt", "root/folder2", new byte[] {})
    };
 // @formatter:on

    return Arrays.stream(arr).collect(Collectors.toSet());
  }



}
