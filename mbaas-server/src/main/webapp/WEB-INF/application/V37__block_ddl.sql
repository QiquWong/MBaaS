CREATE TABLE block (

  block_id         VARCHAR(100) NOT NULL,
  code             VARCHAR(100) NOT NULL,
  title            VARCHAR(100) NOT NULL,
  user_id          VARCHAR(100) NOT NULL,
  description      VARCHAR(255) NOT NULL,
  javascript       TEXT,
  html             TEXT,
  stage_javascript TEXT,
  stage_html       TEXT,
  modified         BIT(1)       NOT NULL,
  date_created     DATETIME     NOT NULL,
  date_modified    DATETIME     NOT NULL,

  INDEX (title),
  INDEX (description),
  UNIQUE (code),
  INDEX (user_id),
  FULLTEXT (javascript),
  FULLTEXT (html),
  INDEX (modified),
  FULLTEXT (stage_html),
  FULLTEXT (stage_javascript),
  INDEX (date_created),
  INDEX (date_modified),
  PRIMARY KEY (block_id)
);