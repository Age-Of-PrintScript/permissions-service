CREATE TABLE IF NOT EXISTS permissions (
        id   VARCHAR(255) DEFAULT gen_random_uuid()::text PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS roles (
        id   VARCHAR(255) DEFAULT gen_random_uuid()::text PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       VARCHAR(255) NOT NULL REFERENCES roles(id),
    permission_id VARCHAR(255) NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
    );

CREATE TABLE IF NOT EXISTS user_snippet_roles (
    id         VARCHAR(255) DEFAULT gen_random_uuid()::text PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL,
    snippet_id VARCHAR(255) NOT NULL,
    role_id    VARCHAR(255) NOT NULL REFERENCES roles(id),
    UNIQUE (user_id, snippet_id, role_id)
    );

