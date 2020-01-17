-- For Postgres

CREATE TABLE CITY (
  id         SERIAL PRIMARY KEY,
  name VARCHAR(30),
  state  VARCHAR(30),
  country  VARCHAR(30),
  map  VARCHAR(30)
);
