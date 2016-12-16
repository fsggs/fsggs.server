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

#m3
CALL PROC_DROP_FOREIGN_KEY('game_characters', 'fk_game_characters_auth_user');
CALL PROC_DROP_FOREIGN_KEY('game_map_solars', 'fk_game_map_solars_characters');
CALL PROC_DROP_FOREIGN_KEY('game_map_galaxies', 'fk_game_map_galaxies_characters');
CALL PROC_DROP_FOREIGN_KEY('game_map_universes', 'fk_game_map_universes_characters');

#m4
CALL PROC_DROP_FOREIGN_KEY('game_map_objects', 'fk_game_map_objects_characters');

DROP PROCEDURE IF EXISTS PROC_DROP_FOREIGN_KEY;

DROP TABLE IF EXISTS `game_characters`;

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
