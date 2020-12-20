package com.bc92.directoryservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.dto.FileDTO;
import com.bc92.directoryservice.dto.NodeDTO;
import com.bc92.directoryservice.dto.NodeDTO.DirElementType;
import com.bc92.directoryservice.model.Path;
import com.bc92.directoryservice.repo.DirectoryRepository;
import com.bc92.directoryservice.repo.FileRepository;

class FileServiceTest {

  private DirectoryRepository directoryRepo;

  private FileRepository fileRepo;

  private FileService fileService;

  private final String username = "TestOwner";

  private final FileDTO fileDTO = new FileDTO(DirElementType.FILE, username, "myText.txt",
      "/root/myText.txt", "/root", new byte[] {1, 2, 3});

  private String fullPath;

  @BeforeEach
  void setUp() {
    directoryRepo = Mockito.mock(DirectoryRepository.class);
    fileRepo = Mockito.mock(FileRepository.class);

    fullPath = Path.escapeSlashes(fileDTO.getFullPath());

    fileService = new FileService(directoryRepo, fileRepo);
  }

  @Test
  void testReadFile() {
    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(fileDTO);

    ByteArrayResource result =
        (ByteArrayResource) fileService.readFile(fileDTO.getFullPath(), username);

    assertEquals(fileDTO.getFileBytes().array(), result.getByteArray());
  }

  @Test
  void testReadFileNotFound() {
    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(null);

    ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
      fileService.readFile("/root/myText.txt", username);
    });

    assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());

  }

  @Test
  void testUploadFile() {

    File file = new File("/root", "myText.txt", new byte[] {1, 2, 3});

    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(null);
    when(directoryRepo.findFolderByFullPathAndOwner(username, Path.escapeSlashes("/root")))
        .thenReturn(new NodeDTO());

    fileService.uploadFile(file, username);

    verify(fileRepo, times(1)).save(fileDTO);
  }

  @Test
  void testUploadFileConflict() {

    File file = new File("/root", "myText.txt", new byte[] {1, 2, 3});

    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(fileDTO);

    ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
      fileService.uploadFile(file, username);
    });

    assertEquals(HttpStatus.CONFLICT, result.getStatus());
  }

  @Test
  void testUploadFileBadRequest() {

    File file = new File("/root", "myText.txt", new byte[] {1, 2, 3});

    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(null);
    when(directoryRepo.findFolderByFullPathAndOwner(username, fullPath)).thenReturn(null);

    ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
      fileService.uploadFile(file, username);
    });

    assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
  }

  @Test
  void testUpdateFile() {

    File file = new File("/root", "myText.txt", new byte[] {1, 2, 3});
    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(fileDTO);

    fileService.updateFile(file, username);

    verify(fileRepo, times(1)).save(fileDTO);

  }

  @Test
  void testUpdateFileNotFound() {

    File file = new File("/root", "myText.txt", new byte[] {1, 2, 3});
    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(null);

    ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
      fileService.updateFile(file, username);
    });

    assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());

  }

  @Test
  void testDeleteFile() {
    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(fileDTO);

    fileService.deleteFile("/root/myText.txt", username);

    verify(fileRepo, times(1)).delete(fileDTO);
  }

  @Test
  void testDeleteFileNotFound() {
    when(fileRepo.findFileByFullPathAndOwner(username, fullPath)).thenReturn(null);

    ResponseStatusException result = assertThrows(ResponseStatusException.class, () -> {
      fileService.deleteFile("/root/myText.txt", username);
    });

    assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
  }

}
