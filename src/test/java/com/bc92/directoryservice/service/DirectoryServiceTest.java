package com.bc92.directoryservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.dto.NodeDTO.DirElementType;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.repo.DirectoryRepository;

class DirectoryServiceTest {

  private DirectoryRepository dirRepo;

  private DirectoryService dirService;

  private final String testOwner = "TestOwner";

  // @formatter:off
  private final NodeDTO[] dirElements = {
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder1", "/root/folder1", "/root"),
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder2", "/root/folder2", "/root"),
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder3", "/root/folder1/folder3", "/root/folder1/"),
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder4", "/root/folder2/folder4", "/root/folder2/"),
      new NodeDTO(DirElementType.FILE, testOwner, "myFile.jpg", "/root/folder1/myFile.jpg", "/root/folder1"),
      new NodeDTO(DirElementType.FILE, testOwner, "myText.txt", "/root/folder2/myText.txt", "/root/folder2")
  };
  // @formatter:on

  @BeforeEach
  void setUp() {
    dirRepo = Mockito.mock(DirectoryRepository.class);
    dirService = new DirectoryService(dirRepo);
    when(dirRepo.findByOwner(testOwner)).thenReturn(Lists.newArrayList(dirElements));
  }


  @Test
  void testCreateFolder() {
    Folder folder = new Folder("folder3", "/root");

    ReadFolder result = dirService.createFolder(folder, testOwner);

    assertEquals("/root/folder3", result.getFullPath());
  }

  @Test
  void testReadFolder() {
    ReadFolder result = dirService.readFolder("/root/folder1", testOwner);

    assertEquals(dirElements[0].getFullPath(), result.getFullPath());
    assertEquals(dirElements[4].getDiscriminator(), result.getFiles().toArray()[0]);
    assertEquals(dirElements[2].getDiscriminator(), result.getChildFolders().toArray()[0]);
  }

  @Test
  void testDeleteFolder() {

    Directory result = dirService.deleteFolder("/root/folder1", testOwner);

    assertFalse(
        result.flatten().stream().anyMatch(elem -> elem.getDiscriminator().equals("folder1")
            || elem.getFullPath().contains("folder1") || elem.getParentPath().contains("folder1")),
        "No Element should reference deleted folder");


  }


}
