package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashSet;
import java.util.Set;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.dto.DirElementDTO.DirElementType;

class DirectoryTest {

  Directory dir;

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
      dir.getFolder("root/folder2/invalid");
    });
  }

  @Test
  void getFolder_pathTooLong_throwError() {
    dir = this.getDirectory();

    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder2/folder3/tooLong");
    });
  }

  @Test
  void createFolder_validInput_Success() {
    dir = this.getDirectory();

    dir.createFolder("root/folder2/folder3/folder4");
    DirectoryNode foundNode = dir.getFolder("root/folder2/folder3/folder4");

    assertEquals("folder4", foundNode.getDiscriminator());
  }

  @Test
  void createFolder_pathTooLong_throwError() {
    dir = this.getDirectory();

    dir.createFolder("root/folder2/folder3/folder4");
    DirectoryNode foundNode = dir.getFolder("root/folder2/folder3/folder4");

    assertEquals("folder4", foundNode.getDiscriminator());
  }

  @Test
  void deleteFolder_validInput_Success() {
    dir = this.getDirectory();

    DirectoryNode deletedNode = dir.deleteFolder("root/folder2");

    assertEquals("folder2", deletedNode.getDiscriminator());
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder2");
    });
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder2/folder3");
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
      dir.deleteFolder("root/folder2/folder3/folder4");
    });
  }

  @Test
  void updateFolder_validInput_Success() {
    dir = this.getDirectory();

    DirectoryNode updatedNode = dir.updateFolder("root/folder2", "myUpdate");
    DirectoryNode searchUpdatedNode = dir.getFolder("root/myUpdate");

    assertEquals("myUpdate", updatedNode.getDiscriminator());
    assertEquals("myUpdate", searchUpdatedNode.getDiscriminator());
    assertThrows(InvalidPathException.class, () -> {
      dir.getFolder("root/folder2");
    });
  }

  @Test
  void updateFolder_incorrectPath_throwError() {
    dir = this.getDirectory();

    InvalidPathException ex = assertThrows(InvalidPathException.class, () -> {
      dir.updateFolder("root/folder3/folder3", "myUpdate");
    });

    assertEquals(String.format(InvalidPathException.FOLDER_DOES_NOT_EXIST, "folder3",
        "root/folder3/folder3"), ex.getMessage());
  }

  @Test
  void updateFolder_pathTooLong_throwError() {
    dir = this.getDirectory();

    InvalidPathException ex = assertThrows(InvalidPathException.class, () -> {
      dir.updateFolder("root/folder2/folder3/folder4", "myUpdate");
    });

    assertEquals(String.format(InvalidPathException.FOLDER_DOES_NOT_EXIST, "folder4",
        "root/folder2/folder3/folder4"), ex.getMessage());

  }

  @Test
  void toString_success() {
    dir = this.getDirectory();

    //@formatter:off
    String expected = "{\r\n"
        + "  \"discriminator\" : \"root\",\r\n"
        + "  \"children\" : {\r\n"
        + "    \"folder2\" : {\r\n"
        + "      \"discriminator\" : \"folder2\",\r\n"
        + "      \"children\" : {\r\n"
        + "        \"folder3\" : {\r\n"
        + "          \"discriminator\" : \"folder3\",\r\n"
        + "          \"children\" : { },\r\n"
        + "          \"files\" : { }\r\n"
        + "        }\r\n"
        + "      },\r\n"
        + "      \"files\" : { }\r\n"
        + "    },\r\n"
        + "    \"folder1\" : {\r\n"
        + "      \"discriminator\" : \"folder1\",\r\n"
        + "      \"children\" : { },\r\n"
        + "      \"files\" : { }\r\n"
        + "    }\r\n"
        + "  },\r\n"
        + "  \"files\" : { }\r\n"
        + "}";
    //@formatter:on

    assertEquals(expected, dir.toString());
  }


  @Test
  void of_validElementsSet_resutrsDirectoryTree() {

    Directory dir = Directory.of(this.getElementSet());

    // do bunch of tests on dir

    System.out.println(dir.toString());



  }


  private Directory getDirectory() {
    DirectoryNode root = new DirectoryNode("root");

    root.addChild("folder1");
    root.addChild("folder2").addChild("folder3");

    return new Directory(root);
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

    return new HashSet<DirElementDTO>(Lists.newArrayList(arr));

  }


}
