package com.github.thought2code.mcp.server.jdbc.annotated;

import com.github.thought2code.mcp.annotated.McpApplication;
import com.github.thought2code.mcp.annotated.annotation.McpServerApplication;

@McpServerApplication
public class JdbcMcpStreamableHttpServer {
  public static void main(String[] args) {
    McpApplication.run(
        JdbcMcpStreamableHttpServer.class, args, "mcp-server-streamable-http-mode.yml");
  }
}
