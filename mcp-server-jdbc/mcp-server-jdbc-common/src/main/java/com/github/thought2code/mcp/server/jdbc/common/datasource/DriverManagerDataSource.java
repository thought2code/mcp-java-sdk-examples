package com.github.thought2code.mcp.server.jdbc.common.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DriverManagerDataSource implements DataSource {
  private static final String ENV_JDBC_URL = "JDBC_URL";
  private static final String ENV_JDBC_USERNAME = "JDBC_USERNAME";
  private static final String ENV_JDBC_PASSWORD = "JDBC_PASSWORD";

  private final DataSourceConfig config;

  public DriverManagerDataSource(DataSourceConfig config) {
    this.config = config;
  }

  public static DriverManagerDataSource fromEnv() {
    DataSourceConfig config =
        DataSourceConfig.builder()
            .jdbcUrl(getRequiredEnv(ENV_JDBC_URL))
            .username(System.getenv(ENV_JDBC_USERNAME))
            .password(System.getenv(ENV_JDBC_PASSWORD))
            .build();
    return new DriverManagerDataSource(config);
  }

  @Override
  public Connection getConnection() throws SQLException {
    final String jdbcUrl = config.jdbcUrl();
    final String username = config.username();
    final String password = config.password();
    if (username == null || username.trim().isBlank()) {
      return DriverManager.getConnection(jdbcUrl);
    }
    return DriverManager.getConnection(jdbcUrl, username, password);
  }

  private static String getRequiredEnv(String name) {
    final String value = System.getenv(name);
    if (value == null || value.trim().isBlank()) {
      throw new IllegalStateException(name + " environment variable is required");
    }
    return value;
  }
}
