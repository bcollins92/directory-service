package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.dto.DirElementDTO.DirElementType;

class DirectoryNodeTest {

  // @formatter:off
  private final DirElementDTO[] dirElements = {
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1", "/root/folder1", "/root", new byte[] {}),
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder2", "/root/folder2", "/root", new byte[] {}),
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-1", "/root/folder1/folder1-1", "/root/folder1/", new byte[] {}),
      new DirElementDTO(DirElementType.FOLDER, "TestOwner", "folder1-2", "/root/folder1/folder1-2", "/root/folder1/", new byte[] {}),
      new DirElementDTO(DirElementType.FILE, "TestOwner", "myFile.jpg", "/root/folder2/myFile.jpg", "/root/folder2", new byte[] {}),
      new DirElementDTO(DirElementType.FILE, "TestOwner", "myText.txt", "/root/folder2/myText.txt", "/root/folder2", new byte[] {})
  };
  // @formatter:on


  @Test
  void copy_allowsClientToNavigateAnywhereInDir() {
    DirectoryNode root = this.getDirectoryTree();

    DirectoryNode folder3Copy = root.getChild("folder2").getChild("folder3").copy();

    assertEquals(root.getDiscriminator(), folder3Copy.getParent().getParent().getDiscriminator(),
        "User can access the Root node");
    assertEquals(root.getChild("folder1").getDiscriminator(),
        folder3Copy.getParent().getParent().getChild("folder1").getDiscriminator(),
        "User can traverse the tree, and access the copied node again");
  }



  @Test
  void copy_returnsSameDataAsCopiedNode() {
    DirectoryNode root = new DirectoryNode();
    DirectoryNode child = new DirectoryNode(dirElements[0], root);

    assertEquals(child.getDiscriminator(), dirElements[0].getDiscriminator(),
        "Discriminator must be equal");
    assertTrue(PathParser.pathsAreEqual(child.getFullPath(), dirElements[0].getFullPath()),
        "Full path must be equal");
    assertTrue(PathParser.pathsAreEqual(child.getParentPath(), dirElements[0].getParentPath()),
        "Parent Path must be equal");

  }

  @Test
  void constructor_inconsistantDataProvided_throwsException() {
    DirectoryNode root = new DirectoryNode();
    DirectoryNode folder1 = root.addChild(new DirectoryNode(dirElements[0], root));

    DirElementDTO inconsistantDto = new DirElementDTO(DirElementType.FOLDER, "TestOwner",
        "folder1-1", "/root/folder1/folder1-1", "/root/folder-invalid/", null);

    assertThrows(IllegalArgumentException.class, () -> {
      new DirectoryNode(inconsistantDto, folder1);
    });

  }

  @Test
  void addFile_updatesFileCorrectly() {

  }


  private DirectoryNode getDirectoryTree() {
    DirectoryNode root = new DirectoryNode();

    DirectoryNode folder1 = root.addChild(new DirectoryNode(dirElements[0], root));
    DirectoryNode folder2 = root.addChild(new DirectoryNode(dirElements[1], root));

    folder1.addChild(new DirectoryNode(dirElements[2], folder1));
    folder1.addChild(new DirectoryNode(dirElements[3], folder1));

    folder2.addFile(new File(dirElements[4], folder2));
    folder2.addFile(new File(dirElements[5], folder2));

    return root;
  }


}
