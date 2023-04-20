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

CREATE TABLE t_profile (
  id IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  columnSubject INTEGER NOT NULL,
  columnQuantity INTEGER NOT NULL,
  columnAccountingDate INTEGER NOT NULL,
  columnValueDate INTEGER NOT NULL,
  created TIMESTAMP,
  updated TIMESTAMP
);

INSERT INTO t_profile(id, name, columnSubject, columnQuantity, columnAccountingDate, columnValueDate, created, updated)
VALUES (1, 'EVO Bank', 2, 3, 0, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

CREATE TABLE t_movement (
  id INTEGER PRIMARY KEY,
  subject VARCHAR(1000) NOT NULL,
  quantity NUMERIC(20, 2) NOT NULL,
  valueDate TIMESTAMP,
  accountingDate TIMESTAMP,
  group_id BIGINT,
  profile_id BIGINT NOT NULL,
  created TIMESTAMP,
  CONSTRAINT fk_movement_group FOREIGN KEY(group_id) REFERENCES t_group(id),
  CONSTRAINT fk_movement_profile FOREIGN KEY(profile_id) REFERENCES t_profile(id)
);