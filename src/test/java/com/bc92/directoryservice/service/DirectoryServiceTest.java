package com.bc92.directoryservice.service;

import static org.mockito.Mockito.when;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.dto.NodeDTO.DirElementType;
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.repo.DirectoryRepository;

class DirectoryServiceTest {

  @Mock
  private DirectoryRepository dirRepo;

  @InjectMocks
  private DirectoryService dirService;

  private final String testOwner = "TestOwner";

  // @formatter:off
  private final NodeDTO[] dirElements = {
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder1", "/root/folder1", "/root"),
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder2", "/root/folder2", "/root"),
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder1-1", "/root/folder1/folder1-1", "/root/folder1/"),
      new NodeDTO(DirElementType.FOLDER, testOwner, "folder2-2", "/root/folder2/folder2-2", "/root/folder2/"),
      new NodeDTO(DirElementType.FILE, testOwner, "myFile.jpg", "/root/folder1/myFile.jpg", "/root/folder1"),
      new NodeDTO(DirElementType.FILE, testOwner, "myText.txt", "/root/folder2/myText.txt", "/root/folder2")
  };
  // @formatter:on

  @BeforeEach
  void setUp() {
    when(dirRepo.findByOwner(testOwner)).thenReturn(Lists.newArrayList(dirElements));
  }


  @Test
  void testGetUserDirectory() {
    Directory dir = dirService.getUserDirectory(testOwner);


  }



}
