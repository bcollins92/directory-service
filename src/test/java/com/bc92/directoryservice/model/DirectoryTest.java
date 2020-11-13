package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.dto.DirElementDTO.DirElementType;

class DirectoryTest {

  Directory dir;

  // @formatter:off
  private final DirElementDTO[] arr = {
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1", "/root/folder1", "/root", null),
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder2", "/root/folder2", "/root", null),
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-1", "/root/folder1/folder1-1", "/root/folder1/", null),
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-2", "/root/folder1/folder1-2", "/root/folder1/", null),
      new DirElementDTO(DirElementType.FILE, "TestOwner", "myFile.jpg", "/root/folder2/myFile.jpg", "/root/folder2", new byte[] {}),
      new DirElementDTO(DirElementType.FILE, "TestOwner", "myText.txt", "/root/folder2/myText.txt", "/root/folder2", new byte[] {})
  };
  // @formatter:on

  @Test
  void getFolder_validPath_ReturnFolder() {
    dir = this.getDirectory();
    DirectoryNode foundNode = dir.getFolder("root/folder2/folder3");

    assertEquals("folder3", foundNode.getDiscriminator());
  }

  @Test
  void getFolder_nonExistantFolder_throwError() {
    dir = this.getDirectory();

    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/invalid");
    });
  }

  @Test
  void getFolder_pathTooLong_throwError() {
    dir = this.getDirectory();

    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder2/tooLong");
    });
  }


  @Test
  void deleteFolder_validInput_Success() {
    dir = this.getDirectory();

    DirectoryNode deletedNode = dir.deleteFolder("root/folder1");

    assertEquals("folder1", deletedNode.getDiscriminator());
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder1");
    });
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder1/folder1-1");
    });

  }

  @Test
  void deleteFolder_nonExistantFolder_throwError() {
    dir = this.getDirectory();

    assertThrows(InvalidPathException.class, () -> {
      dir.deleteFolder("root/folder3");
    });
  }

  @Test
  void deleteFolder_pathTooLong_throwError() {
    dir = this.getDirectory();

    assertThrows(InvalidPathException.class, () -> {
      dir.deleteFolder("root/folder1/folder1-1/folder");
    });
  }


  @Test
  void flatten_validElementsSet_matchesWithOriginalSet() {

    Set<DirElementDTO> elements = this.getElementSet();

    Directory dir = Directory.expand(elements);

    Set<DirElementDTO> flattened = dir.flatten();

    assertEquals(elements, flattened,
        "Creating directory and flattening should not change the data");


    List<DirElementDTO> elementsList = Lists.newArrayList(elements);
    List<DirElementDTO> flattenedList = Lists.newArrayList(flattened);

    Collections.sort(elementsList);
    Collections.sort(flattenedList);

    assertEquals(elementsList.size(), flattenedList.size(),
        "Flattened and riginal Elements must be the same size");

    for (int i = 0; i < elementsList.size(); i++) {
      assertEquals(elementsList.get(i).getDiscriminator(), flattenedList.get(i).getDiscriminator(),
          "Discriminator must be equal");
      assertEquals(elementsList.get(i).getFileBytes(), flattenedList.get(i).getFileBytes(),
          "File bytes must be equal");
      assertTrue(PathParser.pathsAreEqual(elementsList.get(i).getFullPath(),
          flattenedList.get(i).getFullPath()), "Full path must be equal");
      assertTrue(PathParser.pathsAreEqual(elementsList.get(i).getParentPath(),
          flattenedList.get(i).getParentPath()), "Parent Path must be equal");

    }

  }


  private Directory getDirectory() {
    return Directory.expand(this.getElementSet());
  }


  private Set<DirElementDTO> getElementSet() {
    return new HashSet<DirElementDTO>(Lists.newArrayList(arr));
  }


}
