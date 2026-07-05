# mcp-server-mysql

This example provides MCP access to MySQL database context through Java SDK servers backed by JDBC.
The packaged server starts in STDIO mode by default.

Available implementations:

- `mcp-server-mysql-annotated-sdk-impl` - Built with the annotated MCP Java SDK
- `mcp-server-mysql-official-sdk-impl` - Built with the official MCP Java SDK
- `mcp-server-mysql-spring-ai-2.x-impl` - Built with Spring AI 2.x

All implementations expose the same MCP resources and tools.

Current MCP capabilities:

- Resources: enabled
- Resource subscriptions and change notifications: enabled by server configuration
- Tools: enabled
- Prompts and completions: not enabled yet

Implemented resource:

- `db://schema` (`get_database_schema`) - Returns database product/version, tables, table comments, column names, and approximate row counts as JSON

Implemented tools:

- `show_create_table` - Returns the MySQL `CREATE TABLE` statement for a table in the current database. Required input: `table_name`

Database configuration for the MCP server process:

- `JDBC_URL` is optional, must start with `jdbc:mysql:`, and defaults to `jdbc:mysql://localhost:3306/bookstore`
- `DB_USERNAME` is optional and defaults to `mcp`
- `DB_PASSWORD` is optional and defaults to `mcp`

Sample database:

- MySQL runs in Docker with initialization data from `mcp-server-mysql-support/src/test/resources/init.sql`
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

Build the annotated SDK MCP server jar from this directory:

```bash
../mvnw -pl mcp-server-mysql-annotated-sdk-impl -am package
```

Build the official SDK MCP server jar from this directory:

```bash
../mvnw -pl mcp-server-mysql-official-sdk-impl -am package
```

Build the Spring AI 2.x MCP server jar from this directory:

```bash
../mvnw -pl mcp-server-mysql-spring-ai-2.x-impl -am package
```

On Windows PowerShell, use `..\mvnw.cmd` instead of `../mvnw`.

The jars are created at:

- `mcp-server-mysql-annotated-sdk-impl/target/mcp-server-mysql-annotated-sdk-impl.jar`
- `mcp-server-mysql-official-sdk-impl/target/mcp-server-mysql-official-sdk-impl.jar`
- `mcp-server-mysql-spring-ai-2.x-impl/target/mcp-server-mysql-spring-ai-2.x-impl.jar`

Use these paths in the examples below:

- `<module>` - absolute path to this `mcp-server-mysql` directory
- `<jar>` - absolute path to any implementation jar, for example:
  - `<module>/mcp-server-mysql-annotated-sdk-impl/target/mcp-server-mysql-annotated-sdk-impl.jar`
  - `<module>/mcp-server-mysql-official-sdk-impl/target/mcp-server-mysql-official-sdk-impl.jar`
  - `<module>/mcp-server-mysql-spring-ai-2.x-impl/target/mcp-server-mysql-spring-ai-2.x-impl.jar`
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
    "mcp-server-mysql": {
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
codex mcp add mcp-server-mysql --env JDBC_URL="<mysql-url>" --env DB_USERNAME=mcp --env DB_PASSWORD=mcp -- java -jar <jar>
```

Or add it to `~/.codex/config.toml` or a trusted project `.codex/config.toml`:

```toml
[mcp_servers.mcp-server-mysql]
command = "java"
args = ["-jar", "<jar>"]

[mcp_servers.mcp-server-mysql.env]
JDBC_URL = "<mysql-url>"
DB_USERNAME = "mcp"
DB_PASSWORD = "mcp"
```

## Cursor

Add this server in Cursor MCP settings:

```json
{
  "mcpServers": {
    "mcp-server-mysql": {
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
    "mcp-server-mysql": {
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
    "mcp-server-mysql": {
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
