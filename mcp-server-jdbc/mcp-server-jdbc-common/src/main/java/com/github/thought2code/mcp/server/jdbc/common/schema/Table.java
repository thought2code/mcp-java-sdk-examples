package com.github.thought2code.mcp.server.jdbc.common.schema;

import java.util.List;
import lombok.Builder;

@Builder
public record Table(String name, String description, List<String> columns) {
  public Table {
    columns = columns == null ? List.of() : List.copyOf(columns);
  }
}
