package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PathTest {

  Path parser;

  @Test
  void constructor_ValidPath_IterationSuccess() {

    String path = "/customer123/myStuff!_-()/folder1/folder2//";
    String[] pathArr = path.split("/");


    parser = new Path(path);

    // start at index 1 because leading '/' gets trimmed by parser
    for (int i = 1; i < pathArr.length; i++) {
      assertEquals(pathArr[i], parser.current().getDiscriminator(),
          "Parser should contain the same strings in order as the path");

      if (i + 1 != pathArr.length) {
        assertTrue(parser.hasNext(), "Parser should have next node");
      } else {
        assertFalse(parser.hasNext(), "Parser shoudld not have next node");
      }

      parser.next();
    }

    assertNull(parser.current(), "Parser's current node should be null");

  }

  @Test
  void constructor_NullOrEmptyPath_Fail() {
    assertThrows(InvalidPathException.class, () -> {
      new Path(null);
    });

    assertThrows(InvalidPathException.class, () -> {
      new Path("");
    });
  }


  @Test
  void constructor_IllegalCharacters_Fail() {
    String pathBackslash = "/folder1/fold\\er2";
    String pathHash = "/folder1/fol#der2";
    String pathQmark = "/folder1/fold?er2";
    String pathDollar = "/folder1/fold$er2";
    String pathDoubleQuote = "/folder1/fold\"er2";
    String pathSingleQuote = "/folder1/fol\'der2";
    String pathGreaterThan = "/folder1/fold>er2";
    String pathLessThan = "/folder1/fold<er2";
    String pathColon = "/folder1/fold:er2";
    String pathSemiColon = "/folder1/fol;der2";
    String pathPipe = "/folder1/fold|er2";
    String pathAstrix = "/folder1/fol*der2";

    String[] paths = new String[] {pathBackslash, pathHash, pathQmark, pathDollar, pathDoubleQuote,
        pathSingleQuote, pathGreaterThan, pathLessThan, pathColon, pathSemiColon, pathPipe,
        pathAstrix};

    for (String path : paths) {
      assertThrows(InvalidPathException.class, () -> {
        new Path(path);
      });
    }
  }

  @Test
  void constructor_SelectedLegalCharacters_Succeed() {
    String pathExmark = "/folder1/fold!er2";
    String pathUnderscore = "/folder1/fol_der2";
    String pathHyphen = "/folder1/fold-er2";
    String pathsAt = "/folder1/fol@der2";
    String pathPeriod = "/folder1/fold.er2";

    String[] paths = new String[] {pathExmark, pathUnderscore, pathHyphen, pathsAt, pathPeriod};

    for (String path : paths) {
      Path parser = new Path(path);
      assertEquals(path.split("/")[2], parser.current().getChild().getDiscriminator(),
          "Path and created discriminator must be the same");
    }
  }

  @Test
  void pathsAreEqual_equalInputs_returnTrue() {
    assertTrue(Path.pathsAreEqual("/root/folderA/", "/root/folderA/"),
        "Paths that are equal path strings must match");
    assertTrue(Path.pathsAreEqual("/root/folderA/", "/root/folderA"),
        "absence of trailing slash should still match");
    assertTrue(Path.pathsAreEqual("/root/folderA", "/root/folderA/"),
        "absence of trailing slash should still match");
  }

  @Test
  void pathsAreEqual_unequalInputs_returnFalse() {
    assertFalse(Path.pathsAreEqual("/root/folderA/", "/root/FolderA"),
        "small difference folder names should matter");
    assertFalse(Path.pathsAreEqual("/root/folderA/", "root/folderA/"),
        "absence leading slash should matter");
    assertFalse(Path.pathsAreEqual("root/folderA/", "/root/folderA/"),
        "absence leading slash should matter");
    assertFalse(Path.pathsAreEqual("root/folderA/", "root/folderA"),
        "absence leading slash should matter");
    assertFalse(Path.pathsAreEqual("root/fol;derA/", "root/fol;derA/"),
        "An invalid path should return false");
  }

  @Test
  void validateDiscriminator_invalidInputs_throwException() {

    String disBackslash = "fold\\er2";
    String disHash = "fol#der2";
    String disQmark = "fold?er2";
    String disDollar = "fold$er2";
    String disDoubleQuote = "fold\"er2";
    String disSingleQuote = "fol\'der2";
    String disGreaterThan = "fold>er2";
    String disLessThan = "fold<er2";
    String disColon = "fold:er2";
    String disSemiColon = "fol;der2";
    String disPipe = "fold|er2";
    String disAstrix = "fol*der2";
    String disForwardslash = "/folder2";

    String[] notValidArray =
        {disBackslash, disHash, disQmark, disDollar, disDoubleQuote, disSingleQuote, disGreaterThan,
            disLessThan, disColon, disSemiColon, disPipe, disAstrix, disForwardslash};

    assertThrows(InvalidDiscriminatorException.class, () -> {
      Path.validateDiscriminator("");
    });
    assertThrows(InvalidDiscriminatorException.class, () -> {
      Path.validateDiscriminator(null);
    });

    for (String invalidDiscriminator : notValidArray) {
      assertThrows(InvalidDiscriminatorException.class, () -> {
        Path.validateDiscriminator(invalidDiscriminator);
      });
    }
  }

  @Test
  void combineParentPathAndDiscriminator_success() {

    assertEquals("/root/folder1/folder2",
        Path.combineParentPathAndDiscriminator("/root/folder1/", "folder2"),
        "Ideally formatted inputs should combine");
    assertEquals("/root/folder1/folder2",
        Path.combineParentPathAndDiscriminator("/root/folder1", "folder2"),
        "No trailing slash for parent path should combine");

  }

}
