package com.bc92.directoryservice.repo;

import java.util.List;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import com.bc92.directoryservice.dto.NodeDTO;


public interface DirectoryRepository extends SolrCrudRepository<NodeDTO, String> {
  // putting here to add in the future, to exclude fileBytes
  // @Query(fields = {"type", "owner", "parentPath", "discriminator", "fullPath", "id"})

  public List<NodeDTO> findByOwner(String owner);

  @Query(value = "type:FOLDER AND owner:?0 AND fullPath_str:?1")
  public NodeDTO findFolderByFullPathAndOwner(String username, String fullPath);

}
