package com.github.thought2code.mcp.server.jdbc.common.schema;

import lombok.Builder;

@Builder
public record Database(String dialect, String version) {}
