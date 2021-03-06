CREATE TABLE eav_varchar (

  eav_varchar_id   VARCHAR(100) NOT NULL,
  application_code VARCHAR(100) NOT NULL,
  collection_id    VARCHAR(100) NOT NULL,
  attribute_id     VARCHAR(100) NOT NULL,
  document_id      VARCHAR(100) NOT NULL,
  attribute_type   VARCHAR(50)  NOT NULL,
  eav_value        VARCHAR(255),

  INDEX (collection_id),
  INDEX (application_code),
  INDEX (attribute_id),
  INDEX (document_id),
  INDEX (attribute_type),
  FULLTEXT (eav_value),
  UNIQUE (collection_id, attribute_id, document_id),
  PRIMARY KEY (eav_varchar_id)
);