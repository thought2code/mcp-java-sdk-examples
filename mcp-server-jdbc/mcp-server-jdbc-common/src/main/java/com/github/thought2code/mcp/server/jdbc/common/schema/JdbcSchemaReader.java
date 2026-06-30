package com.github.thought2code.mcp.server.jdbc.common.schema;

import com.github.thought2code.mcp.server.jdbc.common.datasource.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSchemaReader {
  private static final String TABLE_NAME_PATTERN = "%";
  private static final String COLUMN_NAME_PATTERN = "%";
  private static final String TABLE_TYPE_TABLE = "TABLE";
  private static final String TABLE_TYPE_VIEW = "VIEW";
  private static final String COLUMN_TABLE_CATALOG = "TABLE_CAT";
  private static final String COLUMN_TABLE_SCHEMA = "TABLE_SCHEM";
  private static final String COLUMN_TABLE_NAME = "TABLE_NAME";
  private static final String COLUMN_TABLE_REMARKS = "REMARKS";
  private static final String COLUMN_COLUMN_NAME = "COLUMN_NAME";

  public DatabaseSchema read(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      DatabaseMetaData meta = connection.getMetaData();
      Database database =
          Database.builder()
              .dialect(meta.getDatabaseProductName())
              .version(meta.getDatabaseProductVersion())
              .build();
      List<Table> tables = getTables(meta);
      return DatabaseSchema.builder().database(database).tables(tables).build();
    }
  }

  private List<Table> getTables(DatabaseMetaData meta) throws SQLException {
    List<Table> tables = new ArrayList<>();
    try (ResultSet resultSet =
        meta.getTables(
            null, null, TABLE_NAME_PATTERN, new String[] {TABLE_TYPE_TABLE, TABLE_TYPE_VIEW})) {
      while (resultSet.next()) {
        final String catalog = resultSet.getString(COLUMN_TABLE_CATALOG);
        final String schema = resultSet.getString(COLUMN_TABLE_SCHEMA);
        final String name = resultSet.getString(COLUMN_TABLE_NAME);
        List<String> columns = getColumns(meta, catalog, schema, name);
        Table table =
            Table.builder()
                .name(name)
                .description(resultSet.getString(COLUMN_TABLE_REMARKS))
                .columns(columns)
                .build();
        tables.add(table);
      }
    }
    return tables;
  }

  private List<String> getColumns(
      DatabaseMetaData meta, String catalog, String schema, String table) throws SQLException {
    List<String> columns = new ArrayList<>();
    try (ResultSet resultSet = meta.getColumns(catalog, schema, table, COLUMN_NAME_PATTERN)) {
      while (resultSet.next()) {
        columns.add(resultSet.getString(COLUMN_COLUMN_NAME));
      }
    }
    return columns;
  }
}
