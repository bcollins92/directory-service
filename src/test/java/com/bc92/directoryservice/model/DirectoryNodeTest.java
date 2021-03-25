package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.dto.NodeDTO.DirElementType;

class DirectoryNodeTest {

  // @formatter:off
  private final NodeDTO[] dirElements = {
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder1", "/root/folder1", "/root"),
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder2", "/root/folder2", "/root"),
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder1-1", "/root/folder1/folder1-1", "/root/folder1/"),
      new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder1-2", "/root/folder1/folder1-2", "/root/folder1/"),
      new NodeDTO(DirElementType.FILE, "TestOwner", "myFile.jpg", "/root/folder2/myFile.jpg", "/root/folder2"),
      new NodeDTO(DirElementType.FILE, "TestOwner", "myText.txt", "/root/folder2/myText.txt", "/root/folder2")
  };
  // @formatter:on


  @Test
  void copy_allowsClientToNavigateAnywhereInDir() {
    DirectoryNode root = this.getDirectoryTree();

    DirectoryNode folder12Copy = root.getChild("folder1").getChild("folder1-2").copy();

    assertEquals(root.getDiscriminator(), folder12Copy.getParent().getParent().getDiscriminator(),
        "User can access the Root node");
    assertEquals(root.getChild("folder1").getDiscriminator(),
        folder12Copy.getParent().getParent().getChild("folder1").getDiscriminator(),
        "User can traverse the tree, and access the copied node again");
  }


  @Test
  void copy_returnsSameDataAsCopiedNode() {
    DirectoryNode root = new DirectoryNode();
    DirectoryNode child = new DirectoryNode(dirElements[0], root);

    assertEquals(child.getDiscriminator(), dirElements[0].getDiscriminator(),
        "Discriminator must be equal");
    assertTrue(Path.pathsAreEqual(child.getFullPath(), dirElements[0].getFullPath()),
        "Full path must be equal");
    assertTrue(Path.pathsAreEqual(child.getParentPath(), dirElements[0].getParentPath()),
        "Parent Path must be equal");

  }

  @Test
  void constructor_inconsistantDataProvided_throwsException() {
    DirectoryNode root = new DirectoryNode();
    DirectoryNode folder1 = root.addChild(new DirectoryNode(dirElements[0], root));

    NodeDTO inconsistantDto = new NodeDTO(DirElementType.FOLDER, "TestOwner", "folder1-1",
        "/root/folder1/folder1-1", "/root/folder-invalid/");

    assertThrows(IllegalArgumentException.class, () -> {
      new DirectoryNode(inconsistantDto, folder1);
    });

  }

  private DirectoryNode getDirectoryTree() {
    DirectoryNode root = new DirectoryNode();

    DirectoryNode folder1 = root.addChild(new DirectoryNode(dirElements[0], root));
    DirectoryNode folder2 = root.addChild(new DirectoryNode(dirElements[1], root));

    folder1.addChild(new DirectoryNode(dirElements[2], folder1));
    folder1.addChild(new DirectoryNode(dirElements[3], folder1));

    folder2.addFile(new FileNode(dirElements[4], folder2));
    folder2.addFile(new FileNode(dirElements[5], folder2));

    return root;
  }


}
