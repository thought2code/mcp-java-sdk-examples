package com.github.thought2code.mcp.server.jdbc.annotated;

import com.github.thought2code.mcp.annotated.annotation.McpResource;
import com.github.thought2code.mcp.annotated.enums.MimeType;
import com.github.thought2code.mcp.server.jdbc.common.DatabaseSchema;
import com.github.thought2code.mcp.server.jdbc.common.datasource.DataSourceFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class McpServerResources {
  @McpResource(
      uri = "db://schema",
      name = "get_database_schema",
      description = "Get database schema metadata",
      mimeType = MimeType.APPLICATION_JSON)
  public DatabaseSchema getDatabaseSchema() {
    try {
      return DataSourceFactory.getDataSource().getDatabaseSchema();
    } catch (Exception e) {
      log.error("Failed to get database schema", e);
      throw new IllegalStateException("Failed to get database schema", e);
    }
  }
}
