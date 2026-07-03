package com.github.thought2code.mcp.server.mysql.official;

import com.github.thought2code.mcp.server.mysql.common.datasource.MysqlDataSource;
import com.github.thought2code.mcp.server.mysql.common.exception.DatabaseSchemaResourceException;
import com.github.thought2code.mcp.server.mysql.common.exception.JdbcConfigurationException;
import com.github.thought2code.mcp.server.mysql.common.schema.DatabaseSchema;
import com.github.thought2code.mcp.server.mysql.common.schema.JdbcSchemaReader;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class McpServerResources {
  private final McpJsonMapper jsonMapper;

  public McpServerFeatures.SyncResourceSpecification getDatabaseSchemaResource() {
    McpSchema.Resource resource =
        McpSchema.Resource.builder("db://schema", "get_database_schema")
            .title("Get Database Schema")
            .description("Get database schema metadata")
            .mimeType("application/json")
            .build();
    return new McpServerFeatures.SyncResourceSpecification(
        resource, (exchange, request) -> getDatabaseSchema(resource));
  }

  private McpSchema.ReadResourceResult getDatabaseSchema(McpSchema.Resource resource) {
    try {
      DatabaseSchema schema = new JdbcSchemaReader().read(new MysqlDataSource());
      final String schemaAsJson = jsonMapper.writeValueAsString(schema);
      McpSchema.TextResourceContents contents =
          McpSchema.TextResourceContents.builder(resource.uri(), schemaAsJson).build();
      return McpSchema.ReadResourceResult.builder(List.of(contents)).build();
    } catch (JdbcConfigurationException e) {
      log.error("Invalid JDBC configuration", e);
      throw e;
    } catch (SQLException e) {
      log.error("Failed to get database schema", e);
      throw new DatabaseSchemaResourceException("Failed to read database schema resource", e);
    } catch (IOException e) {
      log.error("Failed to serialize database schema", e);
      throw new DatabaseSchemaResourceException("Failed to serialize database schema resource", e);
    }
  }
}
