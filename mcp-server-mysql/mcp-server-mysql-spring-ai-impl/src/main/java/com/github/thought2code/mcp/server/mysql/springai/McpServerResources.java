package com.github.thought2code.mcp.server.mysql.springai;

import com.github.thought2code.mcp.server.mysql.common.datasource.MysqlDataSource;
import com.github.thought2code.mcp.server.mysql.common.exception.DatabaseSchemaResourceException;
import com.github.thought2code.mcp.server.mysql.common.exception.JdbcConfigurationException;
import com.github.thought2code.mcp.server.mysql.common.schema.DatabaseSchema;
import com.github.thought2code.mcp.server.mysql.common.schema.JdbcSchemaReader;
import io.modelcontextprotocol.spec.McpSchema;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
public class McpServerResources {
  private final JsonMapper jsonMapper = JsonMapper.builder().build();

  @McpResource(
      uri = "db://schema",
      name = "get_database_schema",
      title = "Get Database Schema",
      description = "Get database schema metadata",
      mimeType = "application/json")
  public McpSchema.ReadResourceResult getDatabaseSchema() {
    try {
      DatabaseSchema schema = new JdbcSchemaReader().read(new MysqlDataSource());
      final String schemaAsJson = jsonMapper.writeValueAsString(schema);
      McpSchema.TextResourceContents contents =
          McpSchema.TextResourceContents.builder("db://schema", schemaAsJson).build();
      return McpSchema.ReadResourceResult.builder(List.of(contents)).build();
    } catch (JdbcConfigurationException e) {
      log.error("Invalid JDBC configuration", e);
      throw e;
    } catch (SQLException e) {
      log.error("Failed to get database schema", e);
      throw new DatabaseSchemaResourceException("Failed to read database schema resource", e);
    } catch (JacksonException e) {
      log.error("Failed to serialize database schema", e);
      throw new DatabaseSchemaResourceException("Failed to serialize database schema resource", e);
    }
  }
}
