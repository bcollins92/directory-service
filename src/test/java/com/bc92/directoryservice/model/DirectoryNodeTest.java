package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import com.bc92.directoryservice.dto.DirElementDTO;

class DirectoryNodeTest {

  @Test
  void constructor_InconsistantData_ThrowException() {

    DirectoryNode folder3 = this.getDirectoryTree().getChild("folder2").getChild("folder3");

    DirectoryNode folder2 = this.getDirectoryTree().getChild("folder2");

    DirElementDTO dirElement = new DirElementDTO("TestOwner", folder3);

    InvalidNodeCreationException ex = assertThrows(InvalidPathException.class, () -> {

    });

  }



  @Test
  void copy_AfterCreation_AllowsClientToNavigateAnywhereInDir() {
    DirectoryNode root = this.getDirectoryTree();

    DirectoryNode folder3Copy = root.getChild("folder2").getChild("folder3").copy();

    assertEquals(root.getDiscriminator(), folder3Copy.getParent().getParent().getDiscriminator(),
        "User can access the Root node");
    assertEquals(root.getChild("folder1").getDiscriminator(),
        folder3Copy.getParent().getParent().getChild("folder1").getDiscriminator(),
        "User can traverse the tree, and access the copied node again");
  }

  @Test
  void recurseGetAllPaths_ReturnsHashSetOfAllPaths() {

    DirectoryNode root = this.getDirectoryTree();

    String expected =
        "[/root/folder2/folder3, /root/folder2/folder4, /root, /root/folder2/folder5, /root/folder2, /root/folder2/folder5/folder6, /root/folder1]";

    assertEquals(expected, root.recurseGetAllPaths(new HashSet<>(), "").toString());

  }

  @Test
  addFile_validFile_addsFileToTree(){

  }

  @Test
  addFile_invalidFile_throwsException(){

  }

  @Test
  void recurseFlatten_ReturnsHashSetOfAllNodesInDtoForm() {
    DirectoryNode dir = this.getDirectoryTree();

    Set<DirElementDTO> results = dir.recurseFlatten(new HashSet<>(), "Username");

    Set<String> intendedPaths =
        new HashSet<>(Arrays.asList(new String[] {"root/folder2/folder3", "root/folder2/folder4",
            "root", "root/folder2/folder5", "root/folder2", "root/folder2/folder5/folder6",
            "root/folder1", "root/folder2/folder4/anotherFile.txt", "root/folder1/myfile.txt",
            "root/folder2/folder5/folder6/yetAnotherFile.txt", "root/folder1/myotherFile.txt"}));

    assertEquals(intendedPaths.size(), results.size(),
        "results should be equal in size to intended paths");

    for (DirElementDTO element : results) {
      assertTrue(intendedPaths.contains(element.getFullPath()),
          "Full path should be in intended paths");
      intendedPaths.remove(element.getFullPath());
    }


  }

  private DirectoryNode getDirectoryTree() {
    DirectoryNode root = new DirectoryNode("root");

    root.addChild("folder1");
    root.addChild("folder2").addChild("folder3");
    root.getChild("folder2").addChild("folder4");
    root.getChild("folder2").addChild("folder5").addChild("folder6");

    root.getChild("folder1").addFile(new File("myfile.txt", new byte[] {1, 2, 3}));
    root.getChild("folder1").addFile(new File("myotherFile.txt", new byte[] {1, 2, 3}));

    root.getChild("folder2").getChild("folder4")
        .addFile(new File("anotherFile.txt", new byte[] {1, 2, 3}));

    root.getChild("folder2").getChild("folder5").getChild("folder6")
        .addFile(new File("yetAnotherFile.txt", new byte[] {1, 2, 3}));

    return root;
  }


}
