package com.bc92.directoryservice.repo;

import java.util.List;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import com.bc92.directoryservice.dto.DirElementDTO;


public interface DirectoryRepository extends SolrCrudRepository<DirElementDTO, String> {
  // putting here to add in the future, to exclude fileBytes

  @Query(fields = {"type", "owner", "parentPath", "discriminator", "fullPath", "id"})
  public List<DirElementDTO> findByOwner(String owner);

}
