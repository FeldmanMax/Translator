create table [DATABASE_NAME].public.translations_key
(
  id                  serial      not null constraint translations_key_pkey  primary key,
  key                 varchar(50) not null,
  service             varchar(50) not null,
  feature             varchar(50) not null,
  is_active           boolean     not null default true,
  create_timestamp    timestamp   not null default now(),
  update_timestamp    timestamp   not null default now()
);

ALTER TABLE [DATABASE_NAME].public.translations_key ADD CONSTRAINT UQ_translations_key UNIQUE (key, service, feature);