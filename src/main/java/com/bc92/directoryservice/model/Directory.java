package com.bc92.directoryservice.model;

import java.util.HashSet;
import java.util.Set;
import com.bc92.directoryservice.utilities.JsonUtilities;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Model for the directory. A directory is a tree-like structure made up of {@link DirectoryNode}
 * objects
 * <p>
 * Provides behaviour necessary to manipulate the directory structure & files
 *
 * @author Brian
 *
 */
@Getter
@Setter
@RequiredArgsConstructor
public class Directory {

  final DirectoryNode root;
  DirectoryNode curr;

  /**
   * Get the last folder in a path. for example, to get 'folder2', provide:
   * rootFolder/folder1/folder2
   *
   * @param path - string representing the full path of the folder, for example
   *        username/folder1/folder2
   * @return DirectoryNode - the node that represents the folder
   */
  public DirectoryNode getFolder(final String path) {
    PathParser parser = new PathParser(path);
    curr = this.doGetFolder(parser);

    DirectoryNode copy = curr.copy();
    curr = root;
    return copy;
  }

  /**
   * Create the last folder in the provided path.
   *
   * @param path - string representing the full path of the folder, for example
   *        username/folder1/folder2. Folder2 will be created
   * @return DirectoryNode - the node that represents the folder
   */
  public DirectoryNode createFolder(final String path) {
    PathParser parser = new PathParser(path);
    curr = root;
    // @formatter:off
    /* check if current and parser match
     * check if parser has next element, if not then cannot create as folder already exists
     * shift parser to next
     * check if curr has a child with that matches parser, if not then break and create
     * else shift curr to next
     */ // @formatter:on
    do {
      if (curr == null || !parser.current().getDiscriminator().equals(curr.getDiscriminator())) {
        throw new InvalidPathException(InvalidPathException.FOLDER_DOES_NOT_EXIST,
            parser.current().getDiscriminator(), path);
      }

      if (!parser.hasNext()) {
        throw new InvalidPathException(InvalidPathException.FOLDER_ALREADY_EXISTS,
            parser.current().getDiscriminator(), path);
      }

      parser.next();

      if (curr.getChild(parser.current().getDiscriminator()) == null) {
        break;
      } else {
        curr = curr.getChild(parser.current().getDiscriminator());
      }

    } while (true);

    curr.addChild(parser.current().getDiscriminator());
    curr = curr.getChild(parser.current().getDiscriminator());

    DirectoryNode copy = curr.copy();
    curr = root;
    return copy;
  }

  /**
   * Delete the last folder in the provided path.
   *
   * @param path - string representing the full path of the folder, for example
   *        username/folder1/folder2. Folder2 will be deleted
   * @return DirectoryNode - the node that represents the folder
   */
  public DirectoryNode deleteFolder(final String path) {
    PathParser parser = new PathParser(path);
    curr = this.doGetFolder(parser);

    DirectoryNode copy = curr.copy();
    curr.getParent().removeChild(parser.current().getDiscriminator());
    curr = root;
    return copy;
  }

  /**
   * Update the last folder in the provided path.
   *
   * @param path - string representing the full path of the folder, for example
   *        username/folder1/folder2. Folder2 will be updated
   * @param newDiscriminator - string representing the new discriminator
   * @return DirectoryNode - the node that represents the folder
   */
  public DirectoryNode updateFolder(final String path, final String newDiscriminator) {
    PathParser parser = new PathParser(path);
    curr = this.doGetFolder(parser);

    curr.setDiscriminator(newDiscriminator);
    curr.getParent().getChildren().put(newDiscriminator, curr);
    curr.getParent().getChildren().remove(parser.current().getDiscriminator());

    DirectoryNode copy = curr.copy();
    curr = root;
    return copy;
  }

  /**
   * Performs the tree search to return the specified folder
   *
   * @param parser
   * @return
   */
  private DirectoryNode doGetFolder(final PathParser parser) {
    curr = root;
    // @formatter:off
    /* check if current and parser match
     * check if parser has next element, if not then return folder
     * shift parser to next, and point curr next child element that matches
     */ // @formatter:on
    do {
      if (curr == null || !parser.current().getDiscriminator().equals(curr.getDiscriminator())) {
        throw new InvalidPathException(InvalidPathException.FOLDER_DOES_NOT_EXIST,
            parser.current().getDiscriminator(), parser.getPrimitivePath());
      }

      if (!parser.hasNext()) {
        break;
      }

      parser.next();
      curr = curr.getChild(parser.current().getDiscriminator());
    } while (true);

    return curr;
  }


  @Override
  public String toString() {
    return JsonUtilities.objectToPrettyJson(root);
  }

  public Set<String> getSetOfAllFolders() {
    return root.recurseGetAllPaths(new HashSet<>(), "");
  }

}
