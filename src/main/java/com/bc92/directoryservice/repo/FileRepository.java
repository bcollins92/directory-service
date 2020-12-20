package com.bc92.directoryservice.repo;

import java.util.List;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import com.bc92.directoryservice.dto.FileDTO;


public interface FileRepository extends SolrCrudRepository<FileDTO, String> {

  @Query(value = "type:FILE AND owner:?0 AND fullPath_str:?1")
  public FileDTO findFileByFullPathAndOwner(String username, String fullPath);

  @Query(value = "type:FILE AND owner:?0 AND parentPath_str:?1")
  public List<FileDTO> findFilesByParentPathAndOwner(String username, String parentPath);
}
