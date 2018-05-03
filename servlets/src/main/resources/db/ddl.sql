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

CREATE TABLE game_reports (
  game_id INT(10) NOT NULL,
  tick    INT(10) NOT NULL,
  team1   INT(10) NOT NULL,
  team2   INT(10) NOT NULL,
  team3   INT(10) NOT NULL,
  team4   INT(10) NOT NULL,
  team5   INT(10) NOT NULL,
  team6   INT(10) NOT NULL,
  PRIMARY KEY (game_id, tick)
);
