package com.bc92.directoryservice.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PathParserTest {

  PathParser parser;

  @Test
  void constructor_ValidPath_IterationSuccess() {

    String path = "/customer123/myStuff!_-()/folder1/folder2//";
    String[] pathArr = path.split("/");


    parser = new PathParser(path);

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
      new PathParser(null);
    });

    assertThrows(InvalidPathException.class, () -> {
      new PathParser("");
    });
  }


  @Test
  void constructor_IllegalCharacters_Fail() {
    String pathBackslash = "folder1/fold\\er2";
    String pathHash = "folder1/fol#der2";
    String pathQmark = "folder1/fold?er2";
    String pathDollar = "folder1/fold$er2";
    String pathDoubleQuote = "folder1/fold\"er2";
    String pathSingleQuote = "folder1/fol\'der2";
    String pathGreaterThan = "folder1/fold>er2";
    String pathLessThan = "folder1/fold<er2";
    String pathColon = "folder1/fold:er2";
    String pathSemiColon = "folder1/fol;der2";
    String pathPipe = "folder1/fold|er2";
    String pathAstrix = "folder1/fol*der2";

    String[] paths = new String[] {pathBackslash, pathHash, pathQmark, pathDollar, pathDoubleQuote,
        pathSingleQuote, pathGreaterThan, pathLessThan, pathColon, pathSemiColon, pathPipe,
        pathAstrix};

    for (String path : paths) {
      assertThrows(InvalidPathException.class, () -> {
        new PathParser(path);
      });
    }
  }

  @Test
  void constructor_SelectedLegalCharacters_Succeed() {
    String pathExmark = "folder1/fold!er2";
    String pathUnderscore = "folder1/fol_der2";
    String pathHyphen = "folder1/fold-er2";
    String pathsAt = "folder1/fol@der2";
    String pathPeriod = "folder1/fold.er2";

    String[] paths = new String[] {pathExmark, pathUnderscore, pathHyphen, pathsAt, pathPeriod};

    for (String path : paths) {
      PathParser parser = new PathParser(path);
      assertEquals(path.split("/")[1], parser.current().getChild().getDiscriminator(),
          "Path and created discriminator must be the same");
    }
  }



}
