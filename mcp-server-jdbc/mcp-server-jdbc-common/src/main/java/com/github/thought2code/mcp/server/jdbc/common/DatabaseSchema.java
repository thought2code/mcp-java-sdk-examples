package com.github.thought2code.mcp.server.jdbc.common;

import lombok.Builder;

@Builder
public record DatabaseSchema(String dialect, String version) {}
