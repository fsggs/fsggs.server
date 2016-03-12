#DROP TABLE IF EXISTS `auth_user`;
CREATE TABLE IF NOT EXISTS `game_characters` (
  `id`         BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id`    BIGINT(20) NOT NULL,
  `name`       VARCHAR(255)        DEFAULT NULL,
  `password`   VARCHAR(255)        DEFAULT NULL,
  `race`       INT(11)             DEFAULT NULL,
  `created_at` DATETIME            DEFAULT NULL,
  `updated_at` DATETIME            DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

INSERT INTO `game_characters` (user_id, name, password, race, created_at, updated_at)
VALUES ('1', 'Anequina Nova`est', '', '5', NOW(), NOW());