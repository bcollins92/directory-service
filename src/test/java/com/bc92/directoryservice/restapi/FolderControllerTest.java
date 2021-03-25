package com.bc92.directoryservice.restapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.bc92.directoryservice.service.DirectoryService;
import com.bc92.directoryservice.service.Folder;
import com.bc92.directoryservice.service.ReadFolder;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;
import com.bc92.projectsdk.utils.JsonUtilities;

@RunWith(SpringRunner.class)
@WebMvcTest(FolderController.class)
class FolderControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private DirectoryService dirService;

  //@formatter:off

  @Test
  @WithUserDetails
  void testCreateFolder() throws Exception {

    Folder newFolder = new Folder("folder1", "/root");
    ReadFolder readfolder = new ReadFolder();
    readfolder.setFullPath("/root/folder1");

    when(dirService.createFolder(any(Folder.class), eq("user"))).thenReturn(readfolder);

    MvcResult result = mvc.perform(MockMvcRequestBuilders
        .post(DirectoryServiceConstants.FOLDER_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonUtilities.objectToJson(newFolder)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fullPath").value("/root/folder1"))
        .andReturn();

  }

  @Test
  @WithUserDetails
  void testReadFolder() throws Exception {

    ReadFolder readfolder = new ReadFolder();
    readfolder.setFullPath("/root/folder1");

    when(dirService.readFolder(eq("/root/folder1"), eq("user"))).thenReturn(readfolder);

    MvcResult result = mvc.perform(MockMvcRequestBuilders
        .get(DirectoryServiceConstants.FOLDER_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .param("folder", "/root/folder1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.fullPath").value("/root/folder1"))
        .andReturn();

  }

  @Test
  @WithUserDetails
  void testDeleteFolder() throws Exception {


    MvcResult result = mvc.perform(MockMvcRequestBuilders
        .delete(DirectoryServiceConstants.FOLDER_API_PATH)
        .contentType(MediaType.APPLICATION_JSON)
        .param("folder", "/root/folder1"))
        .andExpect(status().isNoContent())
        .andReturn();

  }

}
