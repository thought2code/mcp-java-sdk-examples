package com.github.thought2code.mcp.server.jdbc.common.datasource;

public class DataSourceFactory {
  public static DataSource getDataSource() {
    return new SqliteDataSource();
  }
}
