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

The JDBC example includes an annotated-sdk implementation backed by a JDBC connection.
The packaged server starts in STDIO mode by default.

Implemented resource:

- `db://schema` - Returns database product/version plus table and column metadata as JSON

JDBC configuration for the MCP server process:

- `JDBC_URL` is required
- `JDBC_USERNAME` is optional
- `JDBC_PASSWORD` is optional

Bundled sample database:

- `mcp-server-jdbc/mcp-server-jdbc-common/src/test/resources/sqlite3.db`

Build the MCP server jar first:

```bash
./mvnw -pl mcp-server-jdbc/mcp-server-jdbc-annotated-sdk -am package
```

On Windows PowerShell, use `.\mvnw.cmd` instead of `./mvnw`.

The jar is created at:

- `mcp-server-jdbc/mcp-server-jdbc-annotated-sdk/target/mcp-server-jdbc-annotated-sdk.jar`

Use these paths in the examples below:

- `<repo>` - absolute path to this repository
- `<jar>` - `<repo>/mcp-server-jdbc/mcp-server-jdbc-annotated-sdk/target/mcp-server-jdbc-annotated-sdk.jar`
- `<sqlite3.db>` - `<repo>/mcp-server-jdbc/mcp-server-jdbc-common/src/test/resources/sqlite3.db`

For Windows paths in JSON/TOML, prefer forward slashes, for example `C:/Users/me/code/mcp-java-sdk-examples/...`.

### MCP Inspector

Start Inspector with the JDBC server as a STDIO server:

```bash
npx @modelcontextprotocol/inspector -e JDBC_URL=jdbc:sqlite:<sqlite3.db> -- java -jar <jar>
```

Open the Inspector URL printed in the terminal, then browse the `db://schema` resource.

### Claude Desktop

Add the server to Claude Desktop MCP settings:

```json
{
  "mcpServers": {
    "jdbc-mcp-server": {
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "jdbc:sqlite:<sqlite3.db>"
      }
    }
  }
}
```

Restart Claude Desktop, then browse the `db://schema` resource from the MCP tools/resources panel.

### Codex

Add the server with the Codex CLI:

```bash
codex mcp add jdbc-mcp-server --env JDBC_URL=jdbc:sqlite:<sqlite3.db> -- java -jar <jar>
```

Or add it to `~/.codex/config.toml` or a trusted project `.codex/config.toml`:

```toml
[mcp_servers.jdbc-mcp-server]
command = "java"
args = ["-jar", "<jar>"]

[mcp_servers.jdbc-mcp-server.env]
JDBC_URL = "jdbc:sqlite:<sqlite3.db>"
```

### Cursor

Add this server in Cursor MCP settings:

```json
{
  "mcpServers": {
    "jdbc-mcp-server": {
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "jdbc:sqlite:<sqlite3.db>"
      }
    }
  }
}
```

### Cline

Open Cline MCP settings and add:

```json
{
  "mcpServers": {
    "jdbc-mcp-server": {
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "jdbc:sqlite:<sqlite3.db>"
      }
    }
  }
}
```

### VS Code

Create or update `.vscode/mcp.json`:

```json
{
  "servers": {
    "jdbc-mcp-server": {
      "type": "stdio",
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "jdbc:sqlite:<sqlite3.db>"
      }
    }
  }
}
```

For databases that require credentials, add them to the MCP server environment:

```json
"env": {
  "JDBC_URL": "jdbc:postgresql://localhost:5432/example",
  "JDBC_USERNAME": "user",
  "JDBC_PASSWORD": "password"
}
```
