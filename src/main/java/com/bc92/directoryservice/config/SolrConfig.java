package com.bc92.directoryservice.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(basePackages = "com.bc92.directoryservice.repo")
@ComponentScan
public class SolrConfig {

  @Bean
  public SolrClient solrClient(@Value("${solr.host}") final String solrHost) {
    return new HttpSolrClient.Builder(solrHost).build();
  }

  @Bean
  public SolrTemplate solrTemplate(final SolrClient client) {
    return new SolrTemplate(client);
  }
}

