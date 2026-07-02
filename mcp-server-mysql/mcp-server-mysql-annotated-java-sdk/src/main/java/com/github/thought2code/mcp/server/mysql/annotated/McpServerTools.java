package com.github.thought2code.mcp.server.mysql.annotated;

import com.github.thought2code.mcp.annotated.annotation.McpTool;
import com.github.thought2code.mcp.annotated.annotation.McpToolParam;
import com.github.thought2code.mcp.server.mysql.common.datasource.MysqlDataSource;
import com.github.thought2code.mcp.server.mysql.common.exception.JdbcConfigurationException;
import com.github.thought2code.mcp.server.mysql.common.exception.TableDdlToolException;
import com.github.thought2code.mcp.server.mysql.common.schema.JdbcTableDdlReader;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class McpServerTools {
  @McpTool(
      name = "show_create_table",
      title = "Show Create Table",
      description = "Return the MySQL CREATE TABLE statement for a table in the current database")
  public String showCreateTable(
      @McpToolParam(name = "table_name", description = "Table name in the current database")
          String tableName) {
    try {
      return new JdbcTableDdlReader().showCreateTable(new MysqlDataSource(), tableName);
    } catch (JdbcConfigurationException e) {
      log.error("Invalid JDBC configuration", e);
      throw e;
    } catch (SQLException e) {
      log.error("Failed to show create table for table: {}", tableName, e);
      throw new TableDdlToolException("Failed to show create table for table: " + tableName, e);
    }
  }
}
