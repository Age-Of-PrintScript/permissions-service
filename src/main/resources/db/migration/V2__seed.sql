INSERT INTO permissions (name) VALUES ('snippet:read'), ('snippet:write'), ('snippet:share')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name) VALUES ('OWNER'), ('EDITOR'), ('VIEWER')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE (r.name = 'OWNER')
   OR (r.name = 'EDITOR' AND p.name IN ('snippet:read', 'snippet:write'))
   OR (r.name = 'VIEWER' AND p.name = 'snippet:read')
    ON CONFLICT DO NOTHING;

INSERT INTO user_snippet_roles (user_id, snippet_id, role_id)
SELECT '123', '124', id FROM roles WHERE name = 'EDITOR'
    ON CONFLICT DO NOTHING;
