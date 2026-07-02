# mcp-server-mysql

This example provides MCP access to MySQL database context through an annotated Java SDK server backed by JDBC.
The packaged server starts in STDIO mode by default.

Current MCP capabilities:

- Resources: enabled
- Resource subscriptions and change notifications: enabled by server configuration
- Tools, prompts, and completions: not enabled yet

Implemented resource:

- `db://schema` (`get_database_schema`) - Returns database product/version, tables, table comments, column names, and approximate row counts as JSON

Database configuration for the MCP server process:

- `JDBC_URL` is optional, must start with `jdbc:mysql:`, and defaults to `jdbc:mysql://localhost:3306/bookstore`
- `DB_USERNAME` is optional and defaults to `mcp`
- `DB_PASSWORD` is optional and defaults to `mcp`

Sample database:

- MySQL runs in Docker with initialization data from `mcp-server-mysql-common/src/test/resources/init.sql`
- The sample schema models a small online bookstore with authors, books, customers, orders, order items, and reviews.

## Run The Sample Database

Start the sample MySQL database from this directory:

```bash
docker compose up -d
```

The container initializes the `bookstore` database on first startup. To recreate the database from `init.sql`, remove the container and start it again:

```bash
docker compose down -v
docker compose up -d
```

## Build

Build the MCP server jar from this directory:

```bash
../mvnw -pl mcp-server-mysql-annotated-java-sdk -am package
```

On Windows PowerShell, use `..\mvnw.cmd` instead of `../mvnw`.

The jar is created at:

- `mcp-server-mysql-annotated-java-sdk/target/mcp-server-mysql-annotated-java-sdk.jar`

Use these paths in the examples below:

- `<module>` - absolute path to this `mcp-server-mysql` directory
- `<jar>` - `<module>/mcp-server-mysql-annotated-java-sdk/target/mcp-server-mysql-annotated-java-sdk.jar`
- `<mysql-url>` - `jdbc:mysql://127.0.0.1:3306/bookstore?allowPublicKeyRetrieval=true&useSSL=false&nullCatalogMeansCurrent=true`

For Windows paths in JSON/TOML, prefer forward slashes, for example `C:/Users/me/code/mcp-java-sdk-examples/mcp-server-mysql/...`.

## Connect From MCP Clients

## MCP Inspector

Start Inspector with the MySQL server as a STDIO server:

```bash
npx @modelcontextprotocol/inspector -e JDBC_URL="<mysql-url>" -e DB_USERNAME=mcp -e DB_PASSWORD=mcp -- java -jar <jar>
```

Open the Inspector URL printed in the terminal, then browse the `db://schema` resource.

## Claude Desktop

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

## Codex

Add the server with the Codex CLI:

```bash
codex mcp add mysql-mcp-server --env JDBC_URL="<mysql-url>" --env DB_USERNAME=mcp --env DB_PASSWORD=mcp -- java -jar <jar>
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

## Cursor

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

## Cline

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

## VS Code

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
