INSERT INTO roles (id, name)
SELECT 1, 'ROLE_ADMIN'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO roles (id, name)
SELECT 2, 'ROLE_USER'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

-- Создаем администратора, если его еще нет
INSERT INTO users (id, username, password, email)
SELECT 1, 'admin', '$2a$12$wsrfQovcFAqqZBGT6ftk8O1eQXnhxo8gifKzDDSm4VB0cHmkNXP2S', 'vonuchka@gmail.com'
    WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- Связываем администратора с ролью ADMIN
INSERT INTO users_roles (user_id, role_id)
SELECT 1, 1 WHERE NOT EXISTS (SELECT 1 FROM users_roles WHERE user_id = 1 AND role_id = 1);

INSERT INTO users_roles(user_id, role_id)
SELECT 1, 2 WHERE NOT EXISTS (SELECT 1 FROM users_roles WHERE user_id = 1 AND role_id = 2);
