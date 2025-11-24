CREATE TABLE user
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(255) NOT NULL COMMENT '用户名'
) COMMENT='用户表';

INSERT INTO user (id, username)
VALUES (1, 'xhj'),
       (2, 'jks'),
       (3, 'xhjks');