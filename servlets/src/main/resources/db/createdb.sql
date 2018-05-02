CREATE TABLE registrations (
  reg_key   VARCHAR(20)      NOT NULL,
  disabled  TINYINT(1)       NOT NULL DEFAULT 0,
  reg_email VARCHAR(60)      NOT NULL DEFAULT '',
  timezone  TINYINT(4)       NOT NULL DEFAULT '0',
  reg_time  DATETIME                  DEFAULT '0000-00-00 00:00:00',
  num_reg   INT(10) UNSIGNED NOT NULL DEFAULT '0',
  name      VARCHAR(80)      NOT NULL DEFAULT '',
  address1  VARCHAR(40)      NOT NULL DEFAULT '',
  address2  VARCHAR(40)      NOT NULL DEFAULT '',
  zip       VARCHAR(40)      NOT NULL DEFAULT '',
  city      VARCHAR(40)      NOT NULL DEFAULT '',
  state     VARCHAR(40)      NOT NULL DEFAULT '',
  country   VARCHAR(40)      NOT NULL DEFAULT '',
  coupon    VARCHAR(8)       NOT NULL DEFAULT '',
  os        CHAR(1)          NOT NULL DEFAULT '',
  affiliate VARCHAR(50)      NOT NULL DEFAULT '',
  shop      VARCHAR(20)      NOT NULL DEFAULT 'swreg',
  ref       VARCHAR(255)     NOT NULL DEFAULT '',
  PRIMARY KEY (reg_key)
);

CREATE TABLE match_user (
  username          VARCHAR(20) NOT NULL,
  reg_key           VARCHAR(20) NOT NULL,
  password          VARCHAR(40) NOT NULL,
  email             VARCHAR(60) NOT NULL,
  last_used_profile INT(10)              DEFAULT NULL,
  banned_until      DATETIME    NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (username),
  UNIQUE KEY (reg_key),
  FOREIGN KEY (reg_key) REFERENCES registrations (reg_key)
);

CREATE TABLE settings (
  property VARCHAR(50)  NOT NULL,
  value    VARCHAR(255) NOT NULL,
  PRIMARY KEY (property)
);

CREATE VIEW match_valid_key AS
  SELECT reg_key
  FROM registrations R
  WHERE NOT R.disabled;

CREATE VIEW match_valid_user AS
  SELECT
    username,
    password
  FROM match_valid_key K, match_user U
  WHERE U.reg_key = K.reg_key AND U.banned_until < NOW();

INSERT INTO settings VALUES ('max_profiles', '5');
INSERT INTO settings VALUES ('min_username_length', '2');
INSERT INTO settings VALUES ('max_username_length', '20');
INSERT INTO settings VALUES ('min_password_length', '6');
INSERT INTO settings VALUES ('max_password_length', '20');
INSERT INTO settings
VALUES ('allowed_chars', 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789^|_.,:;?+={}[]()/\\&%#!<>*@$');
INSERT INTO settings VALUES ('revision', '1116');

CREATE USER matchservlet
  IDENTIFIED BY "match";
GRANT SELECT ON match_valid_user TO matchservlet;
GRANT SELECT ON match_valid_key TO matchservlet;
GRANT SELECT ON settings TO matchservlet;
GRANT INSERT ON match_user TO matchservlet;
