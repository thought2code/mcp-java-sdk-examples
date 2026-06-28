package com.github.thought2code.mcp.server.jdbc.annotated;

import com.github.thought2code.mcp.annotated.McpApplication;
import com.github.thought2code.mcp.annotated.annotation.McpServerApplication;

@McpServerApplication
public class JdbcMcpServer {
  public static void main(String[] args) {
    McpApplication.run(JdbcMcpServer.class, args);
  }
}
