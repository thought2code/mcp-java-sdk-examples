package com.github.thought2code.mcp.server.jdbc.annotated;

import com.github.thought2code.mcp.annotated.annotation.McpResource;
import com.github.thought2code.mcp.annotated.enums.MimeType;
import com.github.thought2code.mcp.server.jdbc.common.datasource.DriverManagerDataSource;
import com.github.thought2code.mcp.server.jdbc.common.exception.DatabaseSchemaResourceException;
import com.github.thought2code.mcp.server.jdbc.common.exception.JdbcConfigurationException;
import com.github.thought2code.mcp.server.jdbc.common.schema.DatabaseSchema;
import com.github.thought2code.mcp.server.jdbc.common.schema.JdbcSchemaReader;
import java.sql.SQLException;
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
      return new JdbcSchemaReader().read(DriverManagerDataSource.fromEnv());
    } catch (JdbcConfigurationException e) {
      throw e;
    } catch (SQLException e) {
      log.error("Failed to get database schema", e);
      throw new DatabaseSchemaResourceException("Failed to read database schema resource", e);
    }
  }
}
