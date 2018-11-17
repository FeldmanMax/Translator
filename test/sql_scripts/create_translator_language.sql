create table [DATABASE_NAME].public.language
(
  id      serial      not null  constraint language_pkey  primary key,
  country varchar(50) not null,
  code    varchar(5)  not null
);

