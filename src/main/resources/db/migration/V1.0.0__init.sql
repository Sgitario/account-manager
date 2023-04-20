CREATE TABLE t_group (
  id IDENTITY PRIMARY KEY,
  name VARCHAR(255),
  created TIMESTAMP,
  updated TIMESTAMP
);

CREATE TABLE t_group_rule (
  id IDENTITY PRIMARY KEY,
  expression VARCHAR(255),
  group_id BIGINT NOT NULL,
  created TIMESTAMP,
  CONSTRAINT fk_group_rule FOREIGN KEY(group_id) REFERENCES t_group(id) ON DELETE CASCADE
);