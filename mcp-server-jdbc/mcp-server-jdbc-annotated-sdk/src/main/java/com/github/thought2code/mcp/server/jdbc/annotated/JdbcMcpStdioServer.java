package com.github.thought2code.mcp.server.jdbc.annotated;

import com.github.thought2code.mcp.annotated.McpApplication;
import com.github.thought2code.mcp.annotated.annotation.McpServerApplication;

@McpServerApplication
public class JdbcMcpStdioServer {
  public static void main(String[] args) {
    McpApplication.run(JdbcMcpStdioServer.class, args, "mcp-server-stdio-mode.yml");
  }
}
