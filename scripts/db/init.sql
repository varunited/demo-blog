DROP TABLE IF EXISTS `stories`;


CREATE TABLE IF NOT EXISTS `stories` (
  `owner_id`     binary(16) NOT NULL,
  `story_id`     binary(16) NOT NULL,
  `story_json`   text       NOT NULL,
  `created_at`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted`   boolean    DEFAULT false,
  PRIMARY KEY (`story_id`)
) DEFAULT CHARSET=utf8mb4;

CREATE INDEX `story_owner`
ON `stories` (`owner_id`);
