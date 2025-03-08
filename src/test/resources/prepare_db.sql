INSERT INTO users (id, username, password, role)
VALUES (101, 'admin@admin.ru', '$2a$10$l8MBW5KdouGuw8BcN2U3quDVjZWHcgrF/IU.I7lvWqzOKdnLNv0pq', 0);
INSERT INTO users (id, username, password, role)
VALUES (102, 'user@user.ru', '$2a$10$/keFcuz3PFtJLyiISzCukOd/guVl9yMK.IRZBOYWiK9eNkFkentIi', 1);
INSERT INTO users (id, username, password, role)
VALUES (111, 'admin2@admin.ru', '$2a$10$l8MBW5KdouGuw8BcN2U3quDVjZWHcgrF/IU.I7lvWqzOKdnLNv0pq', 0);
INSERT INTO users (id, username, password, role)
VALUES (112, 'user2@user.ru', '$2a$10$/keFcuz3PFtJLyiISzCukOd/guVl9yMK.IRZBOYWiK9eNkFkentIi', 1);

INSERT INTO tasks (id, title, description, status, priority, timestamp, author_id, performer_id)
VALUES (101, 'task title', 'task description', 0, 0, 123456789, 101, 102);
INSERT INTO tasks (id, title, description, status, priority, timestamp, author_id, performer_id)
VALUES (102, 'task title2', 'task description2', 0, 0, 1234567890, 101, 112);
INSERT INTO tasks (id, title, description, status, priority, timestamp, author_id, performer_id)
VALUES (103, 'task title3', 'task description3', 0, 0, 12345678901, 111, 112);