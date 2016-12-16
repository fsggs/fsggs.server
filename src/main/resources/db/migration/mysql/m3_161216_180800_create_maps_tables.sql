DROP PROCEDURE IF EXISTS PROC_DROP_FOREIGN_KEY;
DELIMITER $$
CREATE PROCEDURE PROC_DROP_FOREIGN_KEY(IN tablename VARCHAR(64), IN constraintName VARCHAR(64))
  BEGIN
    IF EXISTS(
        SELECT *
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE
          table_schema = DATABASE() AND
          table_name = tableName AND
          constraint_name = constraintName AND
          constraint_type = 'FOREIGN KEY'
    )
    THEN
      SET @query = CONCAT('ALTER TABLE ', tableName, ' DROP FOREIGN KEY ', constraintName, ';');
      PREPARE stmt FROM @query;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
    END IF;
  END$$
DELIMITER ;

CALL PROC_DROP_FOREIGN_KEY('game_map_solars', 'fk_game_map_solars_universes');
CALL PROC_DROP_FOREIGN_KEY('game_map_solars', 'fk_game_map_solars_galaxies');
CALL PROC_DROP_FOREIGN_KEY('game_map_galaxies', 'fk_game_map_galaxies_universes');
CALL PROC_DROP_FOREIGN_KEY('game_maps', 'fk_game_maps_map_solars');
CALL PROC_DROP_FOREIGN_KEY('game_maps', 'fk_game_maps_map_galaxies');
CALL PROC_DROP_FOREIGN_KEY('game_maps', 'fk_game_maps_map_universes');
CALL PROC_DROP_FOREIGN_KEY('game_characters', 'fk_game_characters_auth_user');
CALL PROC_DROP_FOREIGN_KEY('game_map_solars', 'fk_game_map_solars_characters');
CALL PROC_DROP_FOREIGN_KEY('game_map_galaxies', 'fk_game_map_galaxies_characters');
CALL PROC_DROP_FOREIGN_KEY('game_map_universes', 'fk_game_map_universes_characters');

#m4
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_universes');
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_galaxies');
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_solars');

DROP PROCEDURE IF EXISTS PROC_DROP_FOREIGN_KEY;

DROP TABLE IF EXISTS `game_map_universes`;
DROP TABLE IF EXISTS `game_map_galaxies`;
DROP TABLE IF EXISTS `game_map_solars`;
DROP TABLE IF EXISTS `game_maps`;

CREATE TABLE `game_map_universes` (
  `id`         BIGINT(20)              NOT NULL AUTO_INCREMENT,
  `name`       VARCHAR(255)
               CHARACTER SET utf8
               COLLATE utf8_general_ci NOT NULL,
  `title`      VARCHAR(255)
               CHARACTER SET utf8
               COLLATE utf8_general_ci NULL     DEFAULT NULL,
  `owner_id`   BIGINT(20)              NULL     DEFAULT NULL,

  `created_at` DATETIME                         DEFAULT NULL,
  `updated_at` DATETIME                         DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  AUTO_INCREMENT = 1;

CREATE TABLE `game_map_galaxies` (
  `id`          BIGINT(20)              NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NOT NULL,
  `title`       VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NULL     DEFAULT NULL,
  `owner_id`    BIGINT(20)              NULL     DEFAULT NULL,
  `universe_id` BIGINT(20)              NULL     DEFAULT NULL,

  `created_at`  DATETIME                         DEFAULT NULL,
  `updated_at`  DATETIME                         DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  AUTO_INCREMENT = 1;

CREATE TABLE `game_map_solars` (
  `id`          BIGINT(20)              NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NOT NULL,
  `title`       VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NULL     DEFAULT NULL,
  `owner_id`    BIGINT(20)              NULL     DEFAULT NULL,
  `universe_id` BIGINT(20)              NULL     DEFAULT NULL,
  `galaxy_id`   BIGINT(20)              NULL     DEFAULT NULL,

  `created_at`  DATETIME                         DEFAULT NULL,
  `updated_at`  DATETIME                         DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  AUTO_INCREMENT = 1;

CREATE TABLE `game_maps` (
  `id`          BIGINT(20)              NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NOT NULL,
  `metadata`    TEXT CHARACTER SET utf8
                COLLATE utf8_general_ci NULL,
  `universe_id` BIGINT(20)              NULL     DEFAULT NULL,
  `galaxy_id`   BIGINT(20)              NULL     DEFAULT NULL,
  `solar_id`    BIGINT(20)              NULL     DEFAULT NULL,
  `version`     INT(11)                 NOT NULL,

  `created_at`  DATETIME                         DEFAULT NULL,
  `updated_at`  DATETIME                         DEFAULT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  AUTO_INCREMENT = 1;

ALTER TABLE `game_map_solars`
  ADD CONSTRAINT `fk_game_map_solars_universes` FOREIGN KEY (`universe_id`) REFERENCES `game_map_universes` (`id`);
ALTER TABLE `game_map_solars`
  ADD CONSTRAINT `fk_game_map_solars_galaxies` FOREIGN KEY (`galaxy_id`) REFERENCES `game_map_galaxies` (`id`);
ALTER TABLE `game_map_galaxies`
  ADD CONSTRAINT `fk_game_map_galaxies_universes` FOREIGN KEY (`universe_id`) REFERENCES `game_map_universes` (`id`);
ALTER TABLE `game_maps`
  ADD CONSTRAINT `fk_game_maps_map_solars` FOREIGN KEY (`solar_id`) REFERENCES `game_map_solars` (`id`);
ALTER TABLE `game_maps`
  ADD CONSTRAINT `fk_game_maps_map_galaxies` FOREIGN KEY (`galaxy_id`) REFERENCES `game_map_galaxies` (`id`);
ALTER TABLE `game_maps`
  ADD CONSTRAINT `fk_game_maps_map_universes` FOREIGN KEY (`universe_id`) REFERENCES `game_map_universes` (`id`);
ALTER TABLE `game_characters`
  ADD CONSTRAINT `fk_game_characters_auth_user` FOREIGN KEY (`user_id`) REFERENCES `auth_user` (`id`);
ALTER TABLE `game_map_solars`
  ADD CONSTRAINT `fk_game_map_solars_characters` FOREIGN KEY (`owner_id`) REFERENCES `game_characters` (`id`);
ALTER TABLE `game_map_galaxies`
  ADD CONSTRAINT `fk_game_map_galaxies_characters` FOREIGN KEY (`owner_id`) REFERENCES `game_characters` (`id`);
ALTER TABLE `game_map_universes`
  ADD CONSTRAINT `fk_game_map_universes_characters` FOREIGN KEY (`owner_id`) REFERENCES `game_characters` (`id`);

INSERT INTO `game_map_universes` (id, name, title, created_at, updated_at)
VALUES ('1', 'UNI-U1', 'Hanerta', NOW(), NOW());

INSERT INTO `game_map_galaxies` (id, name, title, universe_id, created_at, updated_at)
VALUES ('1', 'GLX-U1G1', 'Milky Way', 1, NOW(), NOW());

INSERT INTO `game_map_solars` (id, name, title, universe_id, galaxy_id, created_at, updated_at)
VALUES ('1', 'GLX-U1G1S1', 'Solar System', 1, 1, NOW(), NOW());
