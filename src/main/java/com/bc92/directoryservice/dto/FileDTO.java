package com.bc92.directoryservice.dto;

import java.nio.ByteBuffer;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.util.Assert;
import com.bc92.directoryservice.model.DirectoryNode;
import com.bc92.directoryservice.model.FileNode;
import com.bc92.directoryservice.service.File;
import com.bc92.directoryservice.service.Folder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for persisting the directory tree nodes
 *
 * @author Brian
 *
 */
@Getter
@Setter
@NoArgsConstructor
@SolrDocument(collection = "test1")
public class FileDTO extends NodeDTO {

  @Indexed(name = "fileBytes")
  private ByteBuffer fileBytes;

  public FileDTO(final DirElementType type, final String owner, final String discriminator,
      final String fullPath, final String parentPath, final byte[] fileBytes) {
    super(type, owner, discriminator, fullPath, parentPath);
    Assert.notNull(fileBytes, "File bytes should not be null");
    this.fileBytes = ByteBuffer.wrap(fileBytes);
  }

  public FileDTO(final String owner, final DirectoryNode node) {
    super(owner, node);
  }

  public FileDTO(final String owner, final File file) {
    super(owner, file);
    Assert.notNull(file.getFileBytes(), "File bytes should not be null");
    fileBytes = ByteBuffer.wrap(file.getFileBytes());
  }

  public FileDTO(final String owner, final FileNode file) {
    super(owner, file);
  }

  public FileDTO(final String owner, final Folder createFolder) {
    super(owner, createFolder);
  }

}
