package com.github.thought2code.mcp.server.jdbc.common.datasource;

import com.github.thought2code.mcp.server.jdbc.common.DataSourceConfig;
import com.github.thought2code.mcp.server.jdbc.common.DatabaseSchema;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface DataSource {
  default DataSourceConfig getConfig() {
    return new DataSourceConfig(
        System.getenv("JDBC_URL"), System.getenv("JDBC_USERNAME"), System.getenv("JDBC_PASSWORD"));
  }

  default String getJdbcUrl() {
    return getConfig().jdbcUrl();
  }

  default Connection getConnection() throws SQLException {
    DataSourceConfig config = getConfig();
    String jdbcUrl = getJdbcUrl();
    if (config != null && config.username() != null && !config.username().isBlank()) {
      return DriverManager.getConnection(jdbcUrl, config.username(), config.password());
    }
    return DriverManager.getConnection(jdbcUrl);
  }

  default DatabaseSchema getDatabaseSchema() throws SQLException {
    try (Connection connection = getConnection()) {
      DatabaseMetaData meta = connection.getMetaData();
      return DatabaseSchema.builder()
          .dialect(meta.getDatabaseProductName())
          .version(meta.getDatabaseProductVersion())
          .build();
    }
  }
}
