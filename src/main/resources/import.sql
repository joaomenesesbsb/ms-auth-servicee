INSERT INTO tb_user (email, password)
VALUES ('admin@email.com', '$2a$10$yrdJKiVvWRwr/3BoIIJXYO.73OV43O09F8xNf0GuzHEzZSFxaPvey');
INSERT INTO tb_user (email, password)
VALUES ('user@email.com', '$2a$10$yrdJKiVvWRwr/3BoIIJXYO.73OV43O09F8xNf0GuzHEzZSFxaPvey');

INSERT INTO tb_role (name)
VALUES ('ROLE_ADMIN');
INSERT INTO tb_role (name)
VALUES ('ROLE_USER');

INSERT INTO tb_user_role (user_id, role_id)
VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id)
VALUES (2, 2);