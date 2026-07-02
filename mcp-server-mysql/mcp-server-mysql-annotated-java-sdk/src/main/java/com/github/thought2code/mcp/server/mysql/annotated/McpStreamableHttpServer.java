package com.github.thought2code.mcp.server.mysql.annotated;

import com.github.thought2code.mcp.annotated.McpApplication;
import com.github.thought2code.mcp.annotated.annotation.McpServerApplication;

@McpServerApplication
public class McpStreamableHttpServer {
  public static void main(String[] args) {
    McpApplication.run(McpStreamableHttpServer.class, args, "mcp-server-streamable-http-mode.yml");
  }
}
