package com.github.thought2code.mcp.server.jdbc.common.datasource;

import lombok.Builder;

@Builder
public record DataSourceConfig(String jdbcUrl, String username, String password) {}
