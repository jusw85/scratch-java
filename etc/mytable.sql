-- Demo for JOOQ
DROP TABLE IF EXISTS mytable;

CREATE TABLE mytable
(
  id                UUID NOT NULL            DEFAULT uuid_generate_v4(),
  created_timestamp TIMESTAMP WITH TIME ZONE DEFAULT now(),
  title             TEXT,
  description       TEXT,
  count             INT,
  ext               JSONB
);
