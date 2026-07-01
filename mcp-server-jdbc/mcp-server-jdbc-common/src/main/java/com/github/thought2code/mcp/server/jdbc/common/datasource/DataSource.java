package com.github.thought2code.mcp.server.jdbc.common.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
  String getJdbcUrl();

  String getUsername();

  String getPassword();

  Connection getConnection() throws SQLException;
}
