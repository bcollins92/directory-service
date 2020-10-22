package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class DirectoryNodeTest {

  @Test
  void copy_AfterCreation_AllowsClientToNavigateAnywhereInDir() {
    DirectoryNode root = this.getDirectory();

    DirectoryNode folder3Copy = root.getChild("folder2").getChild("folder3").copy();

    assertEquals(root.getDiscriminator(), folder3Copy.getParent().getParent().getDiscriminator());
    assertEquals(root.getChild("folder1").getDiscriminator(),
        folder3Copy.getParent().getParent().getChild("folder1").getDiscriminator());
  }

  @Test
  void recurseGetAllPaths_success() {

    DirectoryNode root = this.getDirectory();

    String expected =
        "[/root/folder2/folder3, /root/folder2/folder4, /root, /root/folder2/folder5, /root/folder2, /root/folder2/folder5/folder6, /root/folder1]";

    assertEquals(expected, root.recurseGetAllPaths(new HashSet<>(), "").toString());

  }

  private DirectoryNode getDirectory() {
    DirectoryNode root = new DirectoryNode("root");

    root.addChild("folder1");
    root.addChild("folder2").addChild("folder3");
    root.getChild("folder2").addChild("folder4");
    root.getChild("folder2").addChild("folder5").addChild("folder6");

    return root;
  }

}
