CREATE TABLE message (

  message_id       VARCHAR(100) NOT NULL,
  conversation_id  VARCHAR(100),
  sender_user_id   VARCHAR(100),
  receiver_user_id VARCHAR(100),
  body             VARCHAR(255),
  `read`           BIT(1),
  date_created     DATETIME     NOT NULL,
  date_read        DATETIME,

  INDEX (conversation_id),
  INDEX (sender_user_id),
  INDEX (receiver_user_id),
  INDEX (body),
  INDEX (`read`),
  INDEX (date_created),
  PRIMARY KEY (message_id)

);