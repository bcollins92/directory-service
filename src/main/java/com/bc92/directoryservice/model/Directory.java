package com.bc92.directoryservice.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bc92.directoryservice.dto.DirElementDTO;
import com.bc92.directoryservice.dto.DirElementDTO.DirElementType;
import com.bc92.directoryservice.service.Folder;
import com.bc92.directoryservice.service.ReadFolder;
import com.bc92.directoryservice.service.UpdateFolder;
import com.bc92.projectsdk.utils.JsonUtilities;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  private static final Logger logger = LoggerFactory.getLogger(Directory.class);

  @JsonIgnore
  private DirectoryNode curr;

  private final DirectoryNode root;

  private final String owner;

  public Directory(final String owner) {
    root = new DirectoryNode();
    this.owner = owner;
  }

  /**
   * Return a new {@link #Directory} from the provided set of {@link #DirElementDTO}
   *
   * @param elementsSet - set of elements representing the flattened directory tree
   * @param owner - string of the username of the owner of this directory
   * @return Directory - new Directory object
   */
  public static Directory expand(final Set<DirElementDTO> elementsSet, final String owner) {
    logger.debug("Expanding directory with the following elements {} for user: {}", elementsSet,
        owner);

    List<DirElementDTO> elements = new ArrayList<>(elementsSet);
    Collections.sort(elements);

    Directory newDir = new Directory(owner);

    for (DirElementDTO element : elements) {
      newDir.addDirectoryElement(element);
    }

    return newDir;
  }

  /**
   * Flatten the directory into a set of {@link #DirElementDTO}
   *
   * @return Set<DirElementDTO> - set of elements representing the flattened directory tree
   */
  public Set<DirElementDTO> flatten() {
    return root.recurseFlatten(new HashSet<>(), owner);
  }


  /**
   * Add the provided DirectoryElementDTO to the Directory tree
   *
   * @param element - a DirElementDTO
   */
  public void addDirectoryElement(final DirElementDTO element) {

    DirectoryNode parent = this.doGetFolder(new Path(element.getParentPath()));

    if (element.getType() == DirElementType.FOLDER) {
      parent.addChild(new DirectoryNode(element, parent));
    } else if (element.getType() == DirElementType.FILE) {
      parent.addFile(new FileNode(element, parent));
    }
  }

  /**
   * Create the provided folder.
   *
   * @param newFolder - the new folder to be created
   * @return ReadFolder - the created folder
   */
  public ReadFolder createFolder(final Folder newFolder) {
    newFolder.validate();
    DirElementDTO create = new DirElementDTO(owner, newFolder);
    create.validate();
    DirectoryNode parent = this.getFolder(newFolder.getParentPath());

    if (parent.containsChild(newFolder.getDiscriminator())) {
      logger.error("Folder already exists, cannot add to directory tree");
      throw new InvalidPathException(InvalidPathException.FOLDER_ALREADY_EXISTS,
          newFolder.getDiscriminator(), newFolder.getFullPath());
    }

    DirectoryNode folderNode = parent.addChild(new DirectoryNode(create, parent));

    return new ReadFolder(folderNode);
  }

  /**
   * Read the contents of the provided folder
   *
   * @param readFolder - string of the full path to folder to be read
   * @return ReadFolder - the folder and its contents
   */
  public ReadFolder readFolder(final String readFolder) {
    DirectoryNode folderNode = this.getFolder(readFolder);
    return new ReadFolder(folderNode);
  }

  /**
   * update the provided folder's discriminator. All child files and folders will be subsequently
   * updated
   *
   * @param updateFolder - the folder to be updated
   */
  public void updateFolderDiscriminator(final UpdateFolder updateFolder) {
    updateFolder.validate();

    curr = this.doGetFolder(new Path(updateFolder.getTarget().getFullPath()));
    curr.updateDiscriminatorAndChildren(updateFolder.getNewDiscriminator());
  }


  /**
   * Collects the provided parentFolder and all of its child folders and files
   *
   * @param parentFolder - string of the parent path
   * @return
   */
  public Set<DirElementDTO> getSubDirectory(final String parentFolder) {
    Path parser = new Path(parentFolder);
    curr = this.doGetFolder(parser);

    Set<DirElementDTO> toBeDeleted = curr.recurseFlatten(new HashSet<>(), owner);
    curr = root;
    return toBeDeleted;
  }

  /**
   * Deletes the parent folder along with its contents and children from this Directory Model
   *
   * @param parentFolder - string representing the full path of the folder, for example
   *        username/folder1/folder2. Folder2 and its children will be deleted
   * @return Set<DirElementDTO> - Set of directory elements to be deleted
   */
  public ReadFolder deleteParentAndAllChildren(final String parentFolder) {
    Path parser = new Path(parentFolder);
    curr = this.doGetFolder(parser);

    ReadFolder deleted =
        new ReadFolder(curr.getParent().removeChild(parser.current().getDiscriminator()));
    curr = root;

    return deleted;
  }

  /**
   * Get the last folder in a path. for example, to get 'folder2', provide:
   * rootFolder/folder1/folder2
   *
   * @param path - string representing the full path of the folder, for example
   *        username/folder1/folder2
   * @return DirectoryNode - the node that represents the folder
   */
  protected DirectoryNode getFolder(final String path) {
    Path parser = new Path(path);
    curr = this.doGetFolder(parser);

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
  private DirectoryNode doGetFolder(final Path parser) {
    curr = root;
    // @formatter:off
    /* check if current and parser match
     * check if parser has next element, if not then return folder
     * shift parser to next, and point curr next child element that matches
     */ // @formatter:on
    do {
      if (curr == null || !parser.current().getDiscriminator().equals(curr.getDiscriminator())) {
        logger.error("ElementType is not recognised, cannot add to directory tree");
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
}
