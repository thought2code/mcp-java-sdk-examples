package com.github.thought2code.mcp.server.mysql.official;

import com.github.thought2code.mcp.server.mysql.common.datasource.MysqlDataSource;
import com.github.thought2code.mcp.server.mysql.common.exception.JdbcConfigurationException;
import com.github.thought2code.mcp.server.mysql.common.exception.TableDdlToolException;
import com.github.thought2code.mcp.server.mysql.common.schema.JdbcTableDdlReader;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.TypeRef;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class McpServerTools {
  private final McpJsonMapper jsonMapper;

  public McpServerFeatures.SyncToolSpecification showCreateTableTool() {
    Map<String, Object> inputSchema =
        readInputSchema("schema/show_create_table_tool_input_schema.json");
    McpSchema.Tool tool =
        McpSchema.Tool.builder("show_create_table", inputSchema)
            .title("Show Create Table")
            .description(
                "Return the MySQL CREATE TABLE statement for a table in the current database")
            .build();
    return McpServerFeatures.SyncToolSpecification.builder()
        .tool(tool)
        .callHandler((exchange, request) -> showCreateTable(request))
        .build();
  }

  private McpSchema.CallToolResult showCreateTable(McpSchema.CallToolRequest request) {
    Map<String, Object> arguments = request.arguments();
    final String tableName = arguments.getOrDefault("table_name", "").toString();
    try {
      final String ddl = new JdbcTableDdlReader().showCreateTable(new MysqlDataSource(), tableName);
      McpSchema.TextContent content = McpSchema.TextContent.builder(ddl).build();
      return McpSchema.CallToolResult.builder(List.of(content)).isError(false).build();
    } catch (JdbcConfigurationException e) {
      log.error("Invalid JDBC configuration", e);
      throw e;
    } catch (SQLException e) {
      log.error("Failed to show create table for table: {}", tableName, e);
      throw new TableDdlToolException("Failed to show create table for table: " + tableName, e);
    }
  }

  private Map<String, Object> readInputSchema(String resourcePath) {
    try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
      if (inputStream == null) {
        throw new IllegalStateException("Input schema resource not found: " + resourcePath);
      }
      final String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
      return jsonMapper.readValue(json, new TypeRef<>() {});
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read input schema resource: " + resourcePath, e);
    }
  }
}
