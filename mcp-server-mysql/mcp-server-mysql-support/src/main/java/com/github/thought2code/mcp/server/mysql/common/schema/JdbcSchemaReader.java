package com.github.thought2code.mcp.server.mysql.common.schema;

import com.github.thought2code.mcp.server.mysql.common.datasource.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSchemaReader {
  private static final String SQL_MYSQL_TABLES =
      """
      SELECT TABLE_NAME, TABLE_COMMENT, TABLE_ROWS
      FROM information_schema.tables
      WHERE TABLE_SCHEMA = ? AND TABLE_TYPE = ?
      ORDER BY TABLE_NAME
      """;

  private static final String MYSQL_TABLE_TYPE_BASE_TABLE = "BASE TABLE";

  private static final String COLUMN_TABLE_NAME = "TABLE_NAME";
  private static final String COLUMN_TABLE_COMMENT = "TABLE_COMMENT";
  private static final String COLUMN_TABLE_ROWS = "TABLE_ROWS";

  private static final String COLUMN_NAME_PATTERN = "%";
  private static final String COLUMN_COLUMN_NAME = "COLUMN_NAME";

  public DatabaseSchema read(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData meta = connection.getMetaData();
      final String dialect = meta.getDatabaseProductName();
      final String version = meta.getDatabaseProductVersion();
      Database database = Database.builder().dialect(dialect).version(version).build();
      List<Table> tables = getTables(meta, connection.getCatalog());
      return DatabaseSchema.builder().database(database).tables(tables).build();
    }
  }

  private List<Table> getTables(DatabaseMetaData meta, String catalog) throws SQLException {
    List<Table> tables = new ArrayList<>();
    try (PreparedStatement statement = meta.getConnection().prepareStatement(SQL_MYSQL_TABLES)) {
      statement.setString(1, catalog);
      statement.setString(2, MYSQL_TABLE_TYPE_BASE_TABLE);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          final String name = resultSet.getString(COLUMN_TABLE_NAME);
          final String comment = resultSet.getString(COLUMN_TABLE_COMMENT);
          List<String> columns = getColumns(meta, catalog, name);
          final Long rowCount = resultSet.wasNull() ? null : resultSet.getLong(COLUMN_TABLE_ROWS);
          Table table =
              Table.builder()
                  .name(name)
                  .comment(comment)
                  .columns(columns)
                  .rowCount(rowCount)
                  .build();
          tables.add(table);
        }
      }
    }
    return tables;
  }

  private List<String> getColumns(DatabaseMetaData meta, String catalog, String table)
      throws SQLException {
    List<String> columns = new ArrayList<>();
    try (ResultSet resultSet = meta.getColumns(catalog, null, table, COLUMN_NAME_PATTERN)) {
      while (resultSet.next()) {
        columns.add(resultSet.getString(COLUMN_COLUMN_NAME));
      }
    }
    return columns;
  }
}
