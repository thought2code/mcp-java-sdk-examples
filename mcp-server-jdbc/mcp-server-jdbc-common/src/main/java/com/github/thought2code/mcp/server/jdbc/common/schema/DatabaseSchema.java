package com.github.thought2code.mcp.server.jdbc.common.schema;

import java.util.List;
import lombok.Builder;

@Builder
public record DatabaseSchema(Database database, List<Table> tables) {
  public DatabaseSchema {
    tables = tables == null ? List.of() : List.copyOf(tables);
  }
}
