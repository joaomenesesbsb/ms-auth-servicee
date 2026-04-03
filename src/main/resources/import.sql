INSERT INTO tb_user (email, password) VALUES ('admin@email.com', '$2a$10$iH/F3XdL8IlkbOApuPM7BOTiGvp3GTjnYWXgNsuZfwfKAMx9IE3Ny');
INSERT INTO tb_user (email, password) VALUES ('user@email.com', '$2a$10$iH/F3XdL8IlkbOApuPM7BOTiGvp3GTjnYWXgNsuZfwfKAMx9IE3Ny');

INSERT INTO tb_role (name) VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (name) VALUES ('ROLE_USER');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);