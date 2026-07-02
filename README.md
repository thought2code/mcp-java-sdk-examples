# MCP Java SDK Examples

A collection of MCP examples developed with Java SDKs.

## Requirements

- Java 17 or later
- Maven 3.9.16 or the included Maven Wrapper
- Docker with Docker Compose

## What is MCP?

The [Model Context Protocol (MCP)](https://modelcontextprotocol.io) lets servers expose data and functionality to LLM applications in a standardized way. MCP servers can expose resources, tools, prompts, and more.

## Examples

- [mcp-server-mysql](mcp-server-mysql) - Exposes MySQL database metadata through an MCP resource

## mcp-server-mysql

The MySQL example includes an annotated-sdk implementation backed by a MySQL JDBC connection.
The packaged server starts in STDIO mode by default.

Implemented resource:

- `db://schema` - Returns database product/version plus table and column metadata as JSON

Database configuration for the MCP server process:

- `JDBC_URL` is optional, must start with `jdbc:mysql:`, and defaults to `jdbc:mysql://localhost:3306/bookstore`
- `DB_USERNAME` is optional and defaults to `mcp`
- `DB_PASSWORD` is optional and defaults to `mcp`

Sample database:

- MySQL runs in Docker with initialization data from `mcp-server-mysql/mcp-server-mysql-common/src/test/resources/init.sql`
- The sample schema models a small online bookstore with authors, books, customers, orders, order items, and reviews.

Start the sample MySQL database:

```bash
docker compose -f mcp-server-mysql/docker-compose.yml up -d
```

The container initializes the `bookstore` database on first startup. To recreate the database from `init.sql`, remove the container and start it again:

```bash
docker compose -f mcp-server-mysql/docker-compose.yml down -v
docker compose -f mcp-server-mysql/docker-compose.yml up -d
```

Build the MCP server jar first:

```bash
./mvnw -pl mcp-server-mysql/mcp-server-mysql-annotated-java-sdk -am package
```

On Windows PowerShell, use `.\mvnw.cmd` instead of `./mvnw`.

The jar is created at:

- `mcp-server-mysql/mcp-server-mysql-annotated-java-sdk/target/mcp-server-mysql-annotated-java-sdk.jar`

Use these paths in the examples below:

- `<repo>` - absolute path to this repository
- `<jar>` - `<repo>/mcp-server-mysql/mcp-server-mysql-annotated-java-sdk/target/mcp-server-mysql-annotated-java-sdk.jar`
- `<mysql-url>` - `jdbc:mysql://127.0.0.1:3306/bookstore?allowPublicKeyRetrieval=true&useSSL=false&nullCatalogMeansCurrent=true`

For Windows paths in JSON/TOML, prefer forward slashes, for example `C:/Users/me/code/mcp-java-sdk-examples/...`.

### MCP Inspector

Start Inspector with the MySQL server as a STDIO server:

```bash
npx @modelcontextprotocol/inspector \
  -e JDBC_URL="<mysql-url>" \
  -e DB_USERNAME=mcp \
  -e DB_PASSWORD=mcp \
  -- java -jar <jar>
```

Open the Inspector URL printed in the terminal, then browse the `db://schema` resource.

### Claude Desktop

Add the server to Claude Desktop MCP settings:

```json
{
  "mcpServers": {
    "mysql-mcp-server": {
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "<mysql-url>",
        "DB_USERNAME": "mcp",
        "DB_PASSWORD": "mcp"
      }
    }
  }
}
```

Restart Claude Desktop, then browse the `db://schema` resource from the MCP tools/resources panel.

### Codex

Add the server with the Codex CLI:

```bash
codex mcp add mysql-mcp-server \
  --env JDBC_URL="<mysql-url>" \
  --env DB_USERNAME=mcp \
  --env DB_PASSWORD=mcp \
  -- java -jar <jar>
```

Or add it to `~/.codex/config.toml` or a trusted project `.codex/config.toml`:

```toml
[mcp_servers.mysql-mcp-server]
command = "java"
args = ["-jar", "<jar>"]

[mcp_servers.mysql-mcp-server.env]
JDBC_URL = "<mysql-url>"
DB_USERNAME = "mcp"
DB_PASSWORD = "mcp"
```

### Cursor

Add this server in Cursor MCP settings:

```json
{
  "mcpServers": {
    "mysql-mcp-server": {
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "<mysql-url>",
        "DB_USERNAME": "mcp",
        "DB_PASSWORD": "mcp"
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
    "mysql-mcp-server": {
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "<mysql-url>",
        "DB_USERNAME": "mcp",
        "DB_PASSWORD": "mcp"
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
    "mysql-mcp-server": {
      "type": "stdio",
      "command": "java",
      "args": ["-jar", "<jar>"],
      "env": {
        "JDBC_URL": "<mysql-url>",
        "DB_USERNAME": "mcp",
        "DB_PASSWORD": "mcp"
      }
    }
  }
}
```

For databases that require credentials, add them to the MCP server environment:

```json
"env": {
  "JDBC_URL": "<mysql-url>",
  "DB_USERNAME": "mcp",
  "DB_PASSWORD": "mcp"
}
```
