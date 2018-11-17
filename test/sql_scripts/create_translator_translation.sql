create table [DATABASE_NAME].public.translation
(
  id                  serial        not null constraint translations_key_pkey  primary key,
  lang_id             int           not null,
  tran_key            int           not null,
  translation         varchar(1000) not null,
  is_active           boolean       not null default true,
  create_timestamp    timestamp     not null default now(),
  update_timestamp    timestamp     not null default now()
);

ALTER TABLE [DATABASE_NAME].public.translation ADD CONSTRAINT UQ_translation UNIQUE (lang_id, tran_key, translation);