package com.github.thought2code.mcp.server.mysql.common.datasource;

import com.github.thought2code.mcp.server.mysql.common.exception.JdbcConfigurationException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MysqlDataSource implements DataSource {
  private static final String ENV_JDBC_URL = "JDBC_URL";
  private static final String ENV_DB_USERNAME = "DB_USERNAME";
  private static final String ENV_DB_PASSWORD = "DB_PASSWORD";

  private static final String DEFAULT_JDBC_URL = "jdbc:mysql://localhost:3306/bookstore";
  private static final String DEFAULT_DB_USERNAME = "mcp";
  private static final String DEFAULT_DB_PASSWORD = "mcp";

  private static final String JDBC_URL_PREFIX = "jdbc:mysql:";

  @Override
  public String getJdbcUrl() {
    final String jdbcUrl = System.getenv(ENV_JDBC_URL);
    if (jdbcUrl == null || jdbcUrl.trim().isBlank()) {
      return DEFAULT_JDBC_URL;
    }
    if (jdbcUrl.startsWith(JDBC_URL_PREFIX)) {
      return jdbcUrl.trim();
    }
    throw new JdbcConfigurationException("Invalid JDBC URL: " + jdbcUrl);
  }

  @Override
  public String getUsername() {
    final String username = System.getenv(ENV_DB_USERNAME);
    if (username == null || username.trim().isBlank()) {
      return DEFAULT_DB_USERNAME;
    }
    return username.trim();
  }

  @Override
  public String getPassword() {
    final String password = System.getenv(ENV_DB_PASSWORD);
    if (password == null || password.trim().isBlank()) {
      return DEFAULT_DB_PASSWORD;
    }
    return password.trim();
  }

  @Override
  public Connection getConnection() throws SQLException {
    return DriverManager.getConnection(getJdbcUrl(), getUsername(), getPassword());
  }
}
