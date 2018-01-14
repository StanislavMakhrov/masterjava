DROP TABLE IF EXISTS user_group;
DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TYPE IF EXISTS USER_FLAG;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS project;
DROP TYPE IF EXISTS GROUP_TYPE;
DROP SEQUENCE IF EXISTS common_seq;

CREATE TYPE USER_FLAG AS ENUM ('active', 'deleted', 'superuser');

CREATE TABLE city (
  ref  TEXT PRIMARY KEY,
  name TEXT NOT NULL
);

CREATE SEQUENCE user_seq START 100000;

CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT NOT NULL,
  email     TEXT NOT NULL,
  flag      USER_FLAG NOT NULL,
  city_ref TEXT REFERENCES city (ref) ON UPDATE CASCADE
);

CREATE UNIQUE INDEX email_idx ON users (email);

CREATE SEQUENCE common_seq START 100000;

CREATE TABLE project (
  id        INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  name      TEXT UNIQUE NOT NULL,
  description TEXT
);

CREATE TYPE GROUP_TYPE AS ENUM ('REGISTERING', 'CURRENT', 'FINISHED');

CREATE TABLE groups (
  id         INTEGER PRIMARY KEY DEFAULT nextval('common_seq'),
  name       TEXT UNIQUE NOT NULL,
  type       GROUP_TYPE  NOT NULL,
  project_id INTEGER     NOT NULL REFERENCES project (id)
);

CREATE TABLE user_group (
  user_id  INTEGER NOT NULL REFERENCES users (id) ON DELETE CASCADE,
  group_id INTEGER NOT NULL REFERENCES groups (id),
  CONSTRAINT users_group_idx UNIQUE (user_id, group_id)
);