DROP TABLE IF EXISTS `articles`;


CREATE TABLE IF NOT EXISTS `articles` (
  `user_id`      binary(16) NOT NULL,
  `article_id`   binary(16) NOT NULL,
  `article_json` text       NOT NULL,
  `version`      int        DEFAULT 1,
  `created_at`   datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted`   boolean    DEFAULT false,
  PRIMARY KEY (`article_id`)
) DEFAULT CHARSET=utf8mb4;

CREATE INDEX `article_owner`
ON `articles` (`user_id`);
