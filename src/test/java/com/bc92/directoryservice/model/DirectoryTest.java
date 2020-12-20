package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.dto.NodeDTO.DirElementType;
import com.bc92.directoryservice.service.Folder;
import com.bc92.directoryservice.service.ReadFolder;
import com.bc92.directoryservice.service.UpdateFolder;

class DirectoryTest {

  Directory dir;

  // @formatter:off
  private final NodeDTO[] arr = {
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder1", "/root/folder1", "/root"),
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder2", "/root/folder2", "/root"),
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder1-1", "/root/folder1/folder1-1", "/root/folder1/"),
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder2-2", "/root/folder2/folder2-2", "/root/folder2/"),
      new NodeDTO(DirElementType.FILE, "TestOwner", "myFile.jpg", "/root/folder1/myFile.jpg", "/root/folder1"),
      new NodeDTO(DirElementType.FILE, "TestOwner", "myText.txt", "/root/folder2/myText.txt", "/root/folder2")
  };
  // @formatter:on

  @Test
  void createFolder_validFolder_addFolder() {
    dir = this.getDirectory();
    Folder folder = new Folder("folder2-1", "/root/folder2");

    dir.createFolder(folder);

    DirectoryNode created = dir.getRoot().getChild("folder2").getChild("folder2-1");

    assertNotNull(created, "Created should not be null");
    assertEquals("folder2-1", created.getDiscriminator(),
        "Created folder has correct discriminator");
    assertEquals("/root/folder2", created.getParentPath(), "Created folder has correct parentPath");
    assertEquals("/root/folder2/folder2-1", created.getFullPath(),
        "Created folder has correct fullPath");

  }

  @Test
  void createFolder_invalidFolder_throwsExcepton() {

    dir = this.getDirectory();
    Folder folder = new Folder("folder2-1", "/root/folderInvalid");

    assertThrows(InvalidPathException.class, () -> {
      dir.createFolder(folder);
    });
  }

  @Test
  void readFolder_returnsFolder() {
    dir = this.getDirectory();
    ReadFolder folder = dir.readFolder("/root/folder2");

    assertEquals("/root/folder2", folder.getFullPath(), "Read folder full path must be equal");
    assertTrue(folder.getChildFolders().contains(arr[3].getDiscriminator()),
        "Read folder contrains discriminator of child folder");
    assertTrue(folder.getFiles().contains(arr[5].getDiscriminator()),
        "Read folder contrains discriminator of child file");
  }

  @Test
  void getSubDirectory_returnsCorrectElements() {
    Set<NodeDTO> elements = this.getDirectory().getSubDirectory("/root/folder1");

    Set<NodeDTO> predictedResults = new HashSet<>(Lists.newArrayList(arr[0], arr[2], arr[4]));

    assertEquals(predictedResults, elements, "Elements should equal predicted results");
  }

  @Test
  void getFolder_validPath_ReturnFolder() {
    dir = this.getDirectory();
    DirectoryNode foundNode = dir.getFolder("/root/folder1/folder1-1");

    assertEquals("folder1-1", foundNode.getDiscriminator());
  }

  @Test
  void deleteParentAndAllChildren_validInput_Success() {
    dir = this.getDirectory();

    ReadFolder deletedNode = dir.deleteParentAndAllChildren("/root/folder1");

    assertEquals("/root/folder1", deletedNode.getFullPath());
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder1");
    });
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder1/folder1-1");
    });

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
  void flatten_validElementsSet_matchesWithOriginalSet() {

    Set<NodeDTO> elements = this.getElementSet();

    Directory dir = Directory.expand(elements, "TestOwner");

    Set<NodeDTO> flattened = dir.flatten();

    assertEquals(elements, flattened,
        "Creating directory and flattening should not change the data");


    List<NodeDTO> elementsList = Lists.newArrayList(elements);
    List<NodeDTO> flattenedList = Lists.newArrayList(flattened);

    Collections.sort(elementsList);
    Collections.sort(flattenedList);

    assertEquals(elementsList.size(), flattenedList.size(),
        "Flattened and riginal Elements must be the same size");

    for (int i = 0; i < elementsList.size(); i++) {
      assertEquals(elementsList.get(i).getDiscriminator(), flattenedList.get(i).getDiscriminator(),
          "Discriminator must be equal");
      assertTrue(
          Path.pathsAreEqual(elementsList.get(i).getFullPath(), flattenedList.get(i).getFullPath()),
          "Full path must be equal");
      assertTrue(Path.pathsAreEqual(elementsList.get(i).getParentPath(),
          flattenedList.get(i).getParentPath()), "Parent Path must be equal");

    }

  }

  @Test
  void recurseUpdateParentPath_updatesPathsCorrectly() {
    dir = this.getDirectory();

    dir.updateFolderDiscriminator(
        new UpdateFolder(new Folder("folder1", "/root"), "updatedFolder"));

    DirectoryNode updated = dir.getRoot().getChild("updatedFolder");

    assertNotNull(updated, "updated should not be null");

    assertEquals("/root/updatedFolder", updated.getFullPath(), "Full path should be updated");

    assertEquals("/root/updatedFolder", updated.getChild("folder1-1").getParentPath(),
        "Parent path of child folder must be updated");
    assertEquals("/root/updatedFolder/folder1-1", updated.getChild("folder1-1").getFullPath(),
        "Full path of child folder must be updated");

    assertEquals("/root/updatedFolder", updated.getFile("myFile.jpg").getParentPath(),
        "Parent path of child file must be updated");
    assertEquals("/root/updatedFolder/myFile.jpg", updated.getFile("myFile.jpg").getFullPath(),
        "Full path of child file must be updated");

  }


  private Directory getDirectory() {
    return Directory.expand(this.getElementSet(), "TestOwner");
  }


  private Set<NodeDTO> getElementSet() {
    return new HashSet<NodeDTO>(Lists.newArrayList(arr));
  }


}
