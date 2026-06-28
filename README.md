# MCP Java SDK Examples

A collection of MCP examples developed with Java SDKs.

## Requirements

- Java 17 or later
- Maven 3.9.16 or the included Maven Wrapper

## What is MCP?

The [Model Context Protocol (MCP)](https://modelcontextprotocol.io) lets servers expose data and functionality to LLM applications in a standardized way. MCP servers can expose resources, tools, prompts, and more.

## Examples

- [JDBC Server](mcp-server-jdbc) - Exposes JDBC database metadata through an MCP resource

## JDBC Server

The JDBC example currently includes an annotated-sdk implementation backed by SQLite.

Implemented resource:

- `db://schema` - Returns database product name and version as JSON

Default database configuration:

- `JDBC_URL` defaults to `jdbc:sqlite:./test.db`
- `JDBC_USERNAME` is optional
- `JDBC_PASSWORD` is optional

Run the server:

```powershell
.\mvnw.cmd -pl mcp-server-jdbc/mcp-server-jdbc-annotated-sdk -am compile exec:java -Dexec.mainClass=com.github.thought2code.mcp.server.jdbc.annotated.JdbcMcpServer
```

Run with a custom SQLite database:

```powershell
$env:JDBC_URL = "jdbc:sqlite:C:\path\to\database.db"
.\mvnw.cmd -pl mcp-server-jdbc/mcp-server-jdbc-annotated-sdk -am compile exec:java -Dexec.mainClass=com.github.thought2code.mcp.server.jdbc.annotated.JdbcMcpServer
```
