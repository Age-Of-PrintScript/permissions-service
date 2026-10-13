CREATE TABLE IF NOT EXISTS permissions (
        id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS roles (
        id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
        name VARCHAR(100) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles(id),
    permission_id BIGINT NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
    );

CREATE TABLE IF NOT EXISTS user_snippet_roles (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL,
    snippet_id BIGINT NOT NULL,
    role_id    BIGINT NOT NULL REFERENCES roles(id),
    UNIQUE (user_id, snippet_id, role_id)
    );

