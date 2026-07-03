package com.github.thought2code.mcp.server.mysql.annotated;

import com.github.thought2code.mcp.annotated.McpApplication;
import com.github.thought2code.mcp.annotated.annotation.McpServerApplication;

@McpServerApplication
public class McpStdioServer {
  public static void main(String[] args) {
    McpApplication.run(McpStdioServer.class, args, "mcp-server-stdio-mode.yml");
  }
}
