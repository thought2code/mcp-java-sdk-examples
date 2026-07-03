package com.github.thought2code.mcp.server.mysql.official;

import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import tools.jackson.databind.json.JsonMapper;

public class McpStdioServer {
  private static final String SERVER_NAME = "mcp-server-mysql";
  private static final String SERVER_VERSION = "1.0.0";
  private static final String SERVER_INSTRUCTIONS =
      "You are a helpful AI assistant that can access a database and perform operations on it";

  public static void main(String[] args) throws InterruptedException {
    McpJsonMapper jsonMapper = new JacksonMcpJsonMapper(JsonMapper.builder().build());

    StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(jsonMapper);
    McpSchema.ServerCapabilities serverCapabilities =
        McpSchema.ServerCapabilities.builder().resources(true, true).tools(true).build();

    McpServerResources resources = new McpServerResources(jsonMapper);
    McpServerTools tools = new McpServerTools(jsonMapper);

    McpSyncServer server =
        McpServer.sync(transportProvider)
            .serverInfo(SERVER_NAME, SERVER_VERSION)
            .instructions(SERVER_INSTRUCTIONS)
            .capabilities(serverCapabilities)
            .resources(resources.getDatabaseSchemaResource())
            .tools(tools.showCreateTableTool())
            .build();

    Runtime.getRuntime().addShutdownHook(new Thread(server::closeGracefully));
    Thread.currentThread().join();
  }
}
