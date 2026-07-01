CREATE DATABASE IF NOT EXISTS `mcp`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `mcp`;

DROP TABLE IF EXISTS `prompt`;
DROP TABLE IF EXISTS `tool`;
DROP TABLE IF EXISTS `resource`;

CREATE TABLE `resource` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Resource primary key.',
    `name` VARCHAR(128) NOT NULL COMMENT 'Stable resource name used for discovery and display.',
    `uri` VARCHAR(512) NOT NULL COMMENT 'MCP resource URI exposed to clients.',
    `title` VARCHAR(255) DEFAULT NULL COMMENT 'Human-readable resource title.',
    `description` TEXT DEFAULT NULL COMMENT 'Short explanation of the data represented by this resource.',
    `mime_type` VARCHAR(128) DEFAULT NULL COMMENT 'Optional MIME type returned by the resource, such as application/json or text/plain.',
    `content_source` VARCHAR(64) NOT NULL COMMENT 'Where the resource content comes from, such as static, database, file, or remote.',
    `is_dynamic` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Whether the resource is generated dynamically for each read.',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the resource is currently available to MCP clients.',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when this resource definition was created.',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp when this resource definition was last updated.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_resource_uri` (`uri`),
    UNIQUE KEY `uk_resource_name` (`name`),
    KEY `idx_resource_enabled_name` (`is_enabled`, `name`),
    KEY `idx_resource_content_source` (`content_source`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='MCP resources exposed by the server, including their URI, content type, source, and availability.';

CREATE TABLE `tool` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Tool primary key.',
    `name` VARCHAR(128) NOT NULL COMMENT 'Stable MCP tool name used by clients when invoking the tool.',
    `title` VARCHAR(255) DEFAULT NULL COMMENT 'Human-readable tool title.',
    `description` TEXT DEFAULT NULL COMMENT 'Short explanation of what the tool does and when to use it.',
    `input_schema` JSON NOT NULL COMMENT 'JSON Schema describing the tool input arguments.',
    `output_schema` JSON DEFAULT NULL COMMENT 'Optional JSON Schema describing the tool result payload.',
    `read_only_hint` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Whether the tool is expected to avoid modifying external state.',
    `destructive_hint` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Whether the tool may perform destructive changes.',
    `idempotent_hint` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Whether repeated calls with the same input should have the same effect.',
    `open_world_hint` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the tool may interact with external systems outside the server.',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the tool is currently available to MCP clients.',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when this tool definition was created.',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp when this tool definition was last updated.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tool_name` (`name`),
    KEY `idx_tool_enabled_name` (`is_enabled`, `name`),
    KEY `idx_tool_safety_hints` (`read_only_hint`, `destructive_hint`, `idempotent_hint`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='MCP tools exposed by the server, including invocation schema, safety hints, and availability.';

CREATE TABLE `prompt` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Prompt primary key.',
    `name` VARCHAR(128) NOT NULL COMMENT 'Stable MCP prompt name used by clients when requesting the prompt.',
    `title` VARCHAR(255) DEFAULT NULL COMMENT 'Human-readable prompt title.',
    `description` TEXT DEFAULT NULL COMMENT 'Short explanation of the prompt purpose.',
    `category` VARCHAR(64) NOT NULL COMMENT 'Prompt category, such as analysis, generation, review, or workflow.',
    `argument_schema` JSON DEFAULT NULL COMMENT 'JSON Schema describing supported prompt arguments.',
    `template` TEXT NOT NULL COMMENT 'Prompt template text that may reference declared arguments.',
    `is_enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT 'Whether the prompt is currently available to MCP clients.',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when this prompt definition was created.',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp when this prompt definition was last updated.',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_prompt_name` (`name`),
    KEY `idx_prompt_enabled_name` (`is_enabled`, `name`),
    KEY `idx_prompt_category` (`category`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='MCP prompts exposed by the server, including argument schema, template, category, and availability.';

INSERT INTO `resource` (
    `name`,
    `uri`,
    `title`,
    `description`,
    `mime_type`,
    `content_source`,
    `is_dynamic`,
    `is_enabled`
) VALUES
    (
        'database-schema',
        'mcp://resources/database/schema',
        'Database Schema',
        'Current MySQL database schema summarized for client-side discovery.',
        'application/json',
        'database',
        1,
        1
    ),
    (
        'server-readme',
        'mcp://resources/server/readme',
        'Server README',
        'Operational notes and example usage for the MCP JDBC server.',
        'text/markdown',
        'file',
        0,
        1
    ),
    (
        'component-catalog',
        'mcp://resources/components/catalog',
        'MCP Component Catalog',
        'Catalog of resources, tools, and prompts registered in this server.',
        'application/json',
        'database',
        1,
        1
    );

INSERT INTO `tool` (
    `name`,
    `title`,
    `description`,
    `input_schema`,
    `output_schema`,
    `read_only_hint`,
    `destructive_hint`,
    `idempotent_hint`,
    `open_world_hint`,
    `is_enabled`
) VALUES
    (
        'schema.read',
        'Read Database Schema',
        'Returns table metadata, comments, columns, and approximate row counts for the configured MySQL database.',
        JSON_OBJECT(
            'type', 'object',
            'properties', JSON_OBJECT(
                'includeDisabled', JSON_OBJECT('type', 'boolean', 'default', false)
            )
        ),
        JSON_OBJECT(
            'type', 'object',
            'properties', JSON_OBJECT(
                'tables', JSON_OBJECT('type', 'array')
            )
        ),
        1,
        0,
        1,
        0,
        1
    ),
    (
        'resource.search',
        'Search Resources',
        'Finds registered MCP resources by name, URI, title, or description.',
        JSON_OBJECT(
            'type', 'object',
            'required', JSON_ARRAY('query'),
            'properties', JSON_OBJECT(
                'query', JSON_OBJECT('type', 'string'),
                'limit', JSON_OBJECT('type', 'integer', 'minimum', 1, 'maximum', 50, 'default', 10)
            )
        ),
        JSON_OBJECT(
            'type', 'object',
            'properties', JSON_OBJECT(
                'resources', JSON_OBJECT('type', 'array')
            )
        ),
        1,
        0,
        1,
        0,
        1
    ),
    (
        'component.enable',
        'Enable Component',
        'Enables a resource, tool, or prompt by component type and name.',
        JSON_OBJECT(
            'type', 'object',
            'required', JSON_ARRAY('componentType', 'name'),
            'properties', JSON_OBJECT(
                'componentType', JSON_OBJECT('type', 'string', 'enum', JSON_ARRAY('resource', 'tool', 'prompt')),
                'name', JSON_OBJECT('type', 'string')
            )
        ),
        JSON_OBJECT(
            'type', 'object',
            'properties', JSON_OBJECT(
                'updated', JSON_OBJECT('type', 'boolean')
            )
        ),
        0,
        0,
        1,
        0,
        1
    );

INSERT INTO `prompt` (
    `name`,
    `title`,
    `description`,
    `category`,
    `argument_schema`,
    `template`,
    `is_enabled`
) VALUES
    (
        'schema-summary',
        'Schema Summary',
        'Summarizes MCP component tables for a client or developer.',
        'analysis',
        JSON_OBJECT(
            'type', 'object',
            'properties', JSON_OBJECT(
                'audience', JSON_OBJECT('type', 'string', 'default', 'developer')
            )
        ),
        'Summarize the MCP component schema for {{audience}}, focusing on resources, tools, prompts, and their relationships.',
        1
    ),
    (
        'tool-design-review',
        'Tool Design Review',
        'Reviews a proposed MCP tool definition for clarity, safety hints, and schema quality.',
        'review',
        JSON_OBJECT(
            'type', 'object',
            'required', JSON_ARRAY('toolName', 'inputSchema'),
            'properties', JSON_OBJECT(
                'toolName', JSON_OBJECT('type', 'string'),
                'inputSchema', JSON_OBJECT('type', 'object')
            )
        ),
        'Review the MCP tool {{toolName}}. Check the input schema, output schema, description, and safety hints for client usability.',
        1
    ),
    (
        'resource-description',
        'Resource Description',
        'Drafts a clear description for an MCP resource from its URI and content type.',
        'generation',
        JSON_OBJECT(
            'type', 'object',
            'required', JSON_ARRAY('uri', 'mimeType'),
            'properties', JSON_OBJECT(
                'uri', JSON_OBJECT('type', 'string'),
                'mimeType', JSON_OBJECT('type', 'string')
            )
        ),
        'Write a concise MCP resource description for URI {{uri}} with MIME type {{mimeType}}.',
        1
    );
