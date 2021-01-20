package com.bc92.directoryservice.restapi;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.bc92.directoryservice.model.InvalidPathException;
import com.bc92.directoryservice.service.DirectoryService;
import com.bc92.projectsdk.constants.DirectoryServiceConstants;

@RunWith(SpringRunner.class)
@WebMvcTest(DirectoryController.class)
class DirectoryControllerAdviceTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private DirectoryService dirService;

//@formatter:off

  @Test
  @WithUserDetails
  void testCatchesDirectoryModelException() throws Exception {

    when(dirService.getUserDirectory(eq("user"))).thenThrow(InvalidPathException.class);

    mvc.perform(MockMvcRequestBuilders
        .get(DirectoryServiceConstants.DIRECTORY_API_PATH)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andReturn();

  }

  @Test
  @WithUserDetails
  void testCatchesResponseStatusException() throws Exception {

    when(dirService.getUserDirectory(eq("user"))).thenThrow(new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT));

    mvc.perform(MockMvcRequestBuilders
        .get(DirectoryServiceConstants.DIRECTORY_API_PATH)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isIAmATeapot())
        .andReturn();

  }

  @Test
  @WithUserDetails
  void testCatchesGenericException() throws Exception {

    when(dirService.getUserDirectory(eq("user"))).thenThrow(RuntimeException.class);

    mvc.perform(MockMvcRequestBuilders
        .get(DirectoryServiceConstants.DIRECTORY_API_PATH)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andReturn();

  }
}
