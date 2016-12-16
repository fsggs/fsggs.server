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

DROP PROCEDURE IF EXISTS PROC_DROP_INDEX_KEY;
DELIMITER $$
CREATE PROCEDURE PROC_DROP_INDEX_KEY(IN tablename VARCHAR(128), IN indexName VARCHAR(128))
  BEGIN
    IF ((
          SELECT COUNT(*) AS index_exists
          FROM information_schema.STATISTICS
          WHERE
            table_schema = DATABASE() AND
            table_name = tableName AND
            index_name = indexName
        ) > 0)
    THEN
      SET @query = CONCAT('DROP INDEX ', indexName, ' ON ', tableName, ';');
      PREPARE stmt FROM @query;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;
    END IF;
  END$$
DELIMITER ;

CALL PROC_DROP_INDEX_KEY('game_map_objects', 'idx_game_map_objects');

CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_universes');
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_galaxies');
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_solars');
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_characters');

DROP PROCEDURE IF EXISTS PROC_DROP_FOREIGN_KEY;
DROP PROCEDURE IF EXISTS PROC_DROP_INDEX_KEY;

DROP TABLE IF EXISTS `game_map_objects`;

CREATE TABLE `game_map_objects` (
  `id`          BIGINT(20)              NOT NULL AUTO_INCREMENT,
  `name`        VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NULL     DEFAULT NULL,
  `title`       VARCHAR(255)
                CHARACTER SET utf8
                COLLATE utf8_general_ci NULL     DEFAULT NULL,
  `owner_id`    BIGINT(20)              NULL     DEFAULT NULL,
  `universe_id` BIGINT(20)              NULL     DEFAULT NULL,
  `galaxy_id`   BIGINT(20)              NULL     DEFAULT NULL,
  `solar_id`    BIGINT(20)              NULL     DEFAULT NULL,
  `type_id`     BIGINT(20)              NULL     DEFAULT NULL,
  `posX`        BIGINT(20)              NULL     DEFAULT NULL,
  `posY`        BIGINT(20)              NULL     DEFAULT NULL,
  `posZ`        BIGINT(20)              NULL     DEFAULT NULL,
  `metadata`    TEXT CHARACTER SET utf8
                COLLATE utf8_general_ci NULL     DEFAULT NULL,

  `created_at`  DATETIME                         DEFAULT NULL,
  `updated_at`  DATETIME                         DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_game_map_objects` (`universe_id`, `galaxy_id`, `solar_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  AUTO_INCREMENT = 1;

ALTER TABLE `game_map_objects`
  ADD CONSTRAINT `fk_game_map_objects_universes` FOREIGN KEY (`universe_id`) REFERENCES `game_map_universes` (`id`);
ALTER TABLE `game_map_objects`
  ADD CONSTRAINT `fk_game_map_objects_galaxies` FOREIGN KEY (`galaxy_id`) REFERENCES `game_map_galaxies` (`id`);
ALTER TABLE `game_map_objects`
  ADD CONSTRAINT `fk_game_map_objects_solars` FOREIGN KEY (`solar_id`) REFERENCES `game_map_solars` (`id`);
ALTER TABLE `game_map_objects`
  ADD CONSTRAINT `fk_game_map_objects_characters` FOREIGN KEY (`owner_id`) REFERENCES `game_characters` (`id`);
