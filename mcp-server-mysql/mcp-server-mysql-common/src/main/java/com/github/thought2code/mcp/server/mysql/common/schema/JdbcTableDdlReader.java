package com.github.thought2code.mcp.server.mysql.common.schema;

import com.github.thought2code.mcp.server.mysql.common.datasource.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcTableDdlReader {
  private static final int CREATE_TABLE_COLUMN_INDEX = 2;

  public String showCreateTable(DataSource dataSource, String tableName) throws SQLException {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet =
            statement.executeQuery("SHOW CREATE TABLE " + quoteIdentifier(tableName))) {
      if (resultSet.next()) {
        return resultSet.getString(CREATE_TABLE_COLUMN_INDEX);
      }
      throw new SQLException("SHOW CREATE TABLE returned no rows for table: " + tableName);
    }
  }

  private String quoteIdentifier(String identifier) throws SQLException {
    if (identifier == null || identifier.trim().isBlank()) {
      throw new SQLException("Table name must not be blank");
    }
    final String trimmed = identifier.trim();
    if (trimmed.indexOf('.') >= 0) {
      throw new SQLException("Table name must not be qualified with a database name");
    }
    return "`" + trimmed.replace("`", "``") + "`";
  }
}
