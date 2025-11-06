package org.meetinglog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "org.meetinglog.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Value("${spring.elasticsearch.uris:http://localhost:9200}")
  private String elasticsearchUri;

  @Value("${spring.elasticsearch.username:elastic}")
  private String username;

  @Value("${spring.elasticsearch.password:}")
  private String password;

  @Override
  public ClientConfiguration clientConfiguration() {
    String host = elasticsearchUri
      .replace("http://", "")
      .replace("https://", "");

    return ClientConfiguration.builder()
      .connectedTo(host)
      .withBasicAuth(username, password)
      .build();
  }
}
