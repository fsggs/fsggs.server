#DROP TABLE IF EXISTS `master_server`;
CREATE TABLE `master_server` (
  `id`         BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(255)        DEFAULT NULL,
  `address`    VARCHAR(255)        DEFAULT NULL,
  `created_at` DATETIME            DEFAULT NULL,
  `updated_at` DATETIME            DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

#DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE `auth_user` (
  `id`            BIGINT(20) NOT NULL AUTO_INCREMENT,
  `login`         VARCHAR(255)        DEFAULT NULL,
  `password`      VARCHAR(255)        DEFAULT NULL,
  `session`       INT(11)             DEFAULT NULL,
  `status`        INT(11)             DEFAULT NULL,
  `access`        INT(11)             DEFAULT NULL,
  `token`         INT(11)             DEFAULT NULL,
  `last_login_at` DATETIME            DEFAULT NULL,
  `created_at`    DATETIME            DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `auth_user` (login, password, status, access, last_login_at, created_at)
VALUES ('admin', 'admin', '1', '3', CURRENT_TIME(), CURRENT_TIME());

INSERT INTO `master_server` (name, address, created_at, updated_at)
VALUES ('FSGGS Solar server #1', 'ws://127.0.0.1:32500', CURRENT_TIME(), CURRENT_TIME());