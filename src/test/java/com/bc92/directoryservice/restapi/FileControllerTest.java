package com.bc92.directoryservice.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import com.bc92.directoryservice.service.File;
import com.bc92.directoryservice.service.FileService;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;

@RunWith(SpringRunner.class)
@WebMvcTest(FileController.class)
class FileControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private FileService fileService;

  @Captor
  ArgumentCaptor<File> fileCaptor;

  //@formatter:off

  @Test
  @WithUserDetails
  void testUploadFile() throws Exception {

    byte[] content = new byte[] {1,2,3,4};
    MockMultipartFile file = new MockMultipartFile("file", "fileName",null, content);

    mvc.perform(MockMvcRequestBuilders
        .multipart(DirectoryServiceConstants.FILE_API_PATH)
        .file(file)
        .param("parentPath", "/root")
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isCreated())
        .andReturn();

    verify(fileService).uploadFile(fileCaptor.capture(), eq("user"));

    File captorFile = fileCaptor.getValue();

    assertEquals("fileName", captorFile.getDiscriminator());
    assertEquals("/root", captorFile.getParentPath());
    assertTrue(Arrays.equals(content, captorFile.getFileBytes()));
  }

  @Test
  @WithUserDetails
  void testReadFile() throws Exception {

    byte[] content = new byte[] {1,2,3,4};

    ByteArrayResource file = new ByteArrayResource(content);

    when(fileService.readFile(eq("/root/file1"), eq("user"))).thenReturn(file);

    MvcResult result = mvc.perform(MockMvcRequestBuilders
        .get(DirectoryServiceConstants.FILE_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .param("fullPath", "/root/file1"))
        .andExpect(status().isOk())
        .andReturn();

    assertTrue(Arrays.equals(content, result.getResponse().getContentAsByteArray()));

  }

  @Test
  @WithUserDetails
  void testUpdateFile() throws Exception {

    byte[] content = new byte[] {1,2,3,4};
    MockMultipartFile file = new MockMultipartFile("file", "fileName",null, content);

    RequestPostProcessor postProcessor = request -> {
        request.setMethod("PUT");
        return request;
    };

    mvc.perform(MockMvcRequestBuilders
        .multipart(DirectoryServiceConstants.FILE_API_PATH)
        .file(file)
        .with(postProcessor)
        .param("parentPath", "/root")
        .contentType(MediaType.MULTIPART_FORM_DATA))
        .andExpect(status().isOk())
        .andReturn();

    verify(fileService).updateFile(fileCaptor.capture(), eq("user"));

    File captorFile = fileCaptor.getValue();

    assertEquals("fileName", captorFile.getDiscriminator());
    assertEquals("/root", captorFile.getParentPath());
    assertTrue(Arrays.equals(content, captorFile.getFileBytes()));
  }

  @Test
  @WithUserDetails
  void testDeleteFile() throws Exception {

    mvc.perform(MockMvcRequestBuilders
        .delete(DirectoryServiceConstants.FILE_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .param("fullPath", "/root/file1"))
        .andExpect(status().isNoContent())
        .andReturn();

    verify(fileService).deleteFile("/root/file1", "user");

  }

}
