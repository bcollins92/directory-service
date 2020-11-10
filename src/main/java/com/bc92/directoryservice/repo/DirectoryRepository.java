package com.bc92.directoryservice.repo;

import org.springframework.data.solr.repository.SolrCrudRepository;
import com.bc92.directoryservice.dto.DirElementDTO;


public interface DirectoryRepository extends SolrCrudRepository<DirElementDTO, String> {



}
