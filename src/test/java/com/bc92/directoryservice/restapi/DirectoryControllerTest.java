package com.bc92.directoryservice.restapi;

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
import com.bc92.directoryservice.model.Directory;
import com.bc92.directoryservice.service.DirectoryService;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;

@RunWith(SpringRunner.class)
@WebMvcTest(DirectoryController.class)
class DirectoryControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private DirectoryService dirService;

//@formatter:off

  @Test
  @WithUserDetails
  void testGetUserDirectory() throws Exception {

    Directory dir = new Directory("test");

    when(dirService.getUserDirectory(eq("user"))).thenReturn(dir);

    MvcResult result = mvc.perform(MockMvcRequestBuilders
        .get(DirectoryServiceConstants.DIRECTORY_API_PATH)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.owner").value("test"))
        .andReturn();

  }

}
