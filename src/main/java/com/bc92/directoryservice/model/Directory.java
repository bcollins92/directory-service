package com.bc92.directoryservice.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.dto.DirElementDTO.DirElementType;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import com.bc92.projectsdk.utils.JsonUtilities;
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

  private final DirectoryNode root;
  private DirectoryNode curr;
  private final String owner;

  public Directory(final String owner) {
    root = new DirectoryNode(DirectoryServiceConstants.ROOT_NODE_NAME);
    this.owner = owner;
  }

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

    // memory leak? creating new object references everywhere?
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
  @Deprecated(forRemoval = true)
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
   * Update the last folder in the provided path.
   *
   * @param path - string representing the full path of the folder, for example
   *        username/folder1/folder2. Folder2 will be updated
   * @param newDiscriminator - string representing the new discriminator
   * @return DirectoryNode - the node that represents the folder
   */
  @Deprecated(forRemoval = true)
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

  /**
   * Flatten the directory into a set of {@link #DirElementDTO}
   *
   * @return Set<DirElementDTO> - set of elements representing the flattened directory tree
   */
  public Set<DirElementDTO> flatten() {
    // TODO: figure out how owner is going to set when flattening happens.
    // in theory, owner should be a property of the node when it comes from DB
    // when building a Directory from scratch, owner needs to be an inherent property
    return root.recurseFlatten(new HashSet<>(), owner);
  }

  /**
   * Return a new {@link #Directory} from the provided set of {@link #DirElementDTO}
   *
   * @param elementsSet - set of elements representing the flattened directory tree
   * @return Directory - new Directory object
   */
  public static Directory of(final Set<DirElementDTO> elementsSet) {

    List<DirElementDTO> elements = new ArrayList<>(elementsSet);
    Collections.sort(elements);

    Directory newDir = new Directory(elements.get(0).getOwner());

    for (DirElementDTO element : elements) {
      newDir.addDirectoryElement(element);
    }

    return newDir;
  }


  public void addDirectoryElement(final DirElementDTO element) {

    // memory leak? creating new object references everywhere?
    DirectoryNode parent = this.getFolder(element.getParentPath());

    if (element.getType() == DirElementType.FOLDER) {
      parent.addChild(new DirectoryNode(element, parent));
    } else if (element.getType() == DirElementType.FILE) {
      parent.addFile(new File(element, parent));
    } else {
      throw new InvalidNodeCreationException(InvalidNodeCreationException.ELEMENT_TYPE_UNKNOWN,
          element);
    }

  }



  @Override
  public String toString() {
    return JsonUtilities.objectToPrettyJson(root);
  }

  public Set<String> getSetOfAllFolders() {
    return root.recurseGetAllPaths(new HashSet<>(), "");
  }



}
