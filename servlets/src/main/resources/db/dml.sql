INSERT INTO registrations (
  id, reg_key, disabled, banned, reg_email, timezone, reg_time, num_reg, name, username, password, email,
  address1, address2, zip, city, state, country, coupon, os, affiliate, shop, ref
) VALUES (
  registrations_seq.nextval, '57WS-AAAA-AAAA-AAAA', 0, 0, 'Email', 0, '2018-04-20 17:29:00', 0, 'Name', 'asdf',
  '30a518b67dcd7af15b369ccb1518ab3cad8e8b2c', 'a@b.c', 'Address1', 'Address2', 'Zip', 'City', 'State',
  'Country', 'Coupon', 'O', 'Affiliate', 'Shop', 'Ref'
);

INSERT INTO registrations (
  id, reg_key, disabled, banned, reg_email, timezone, reg_time, num_reg, name, username, password, email,
  address1, address2, zip, city, state, country, coupon, os, affiliate, shop, ref
) VALUES (
  registrations_seq.nextval, 'G35S-AAAA-AAAA-AAAL', 0, 0, 'Email', 0, '2018-04-20 17:29:00', 0, 'Name', NULL, NULL,
  NULL, 'Address1', 'Address2', 'Zip', 'City', 'State',
  'Country', 'Coupon', 'O', 'Affiliate', 'Shop', 'Ref'
);

INSERT INTO settings VALUES ('max_profiles', '5');

INSERT INTO settings VALUES ('min_username_length', '2');

INSERT INTO settings VALUES ('max_username_length', '20');

INSERT INTO settings VALUES ('min_password_length', '6');

INSERT INTO settings VALUES ('max_password_length', '20');

INSERT INTO settings VALUES ('revision', '1116');

INSERT INTO settings
VALUES ('allowed_chars', 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789^|_.,:;?+={}[]()/\\&%#!<>*@$');

INSERT INTO game_reports (game_id, tick, team1, team2, team3, team4, team5, team6)
VALUES (1, 1, 5, 3, 1, 8, 3, 1);

INSERT INTO game_reports (game_id, tick, team1, team2, team3, team4, team5, team6)
VALUES (1, 2, 2, 7, 1, 5, 5, 3);

INSERT INTO game_reports (game_id, tick, team1, team2, team3, team4, team5, team6)
VALUES (1, 3, 4, 2, 0, 3, 7, 1);
