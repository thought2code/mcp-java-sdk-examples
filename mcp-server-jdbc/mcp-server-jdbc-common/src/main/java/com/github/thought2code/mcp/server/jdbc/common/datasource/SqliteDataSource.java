package com.github.thought2code.mcp.server.jdbc.common.datasource;

public class SqliteDataSource implements DataSource {
  @Override
  public String getJdbcUrl() {
    var config = getConfig();
    if (config != null && config.jdbcUrl() != null && !config.jdbcUrl().isBlank()) {
      return config.jdbcUrl();
    }
    return "jdbc:sqlite:./test.db";
  }
}
