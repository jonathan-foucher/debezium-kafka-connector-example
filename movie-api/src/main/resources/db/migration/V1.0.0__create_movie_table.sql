drop table if exists movie;
create table movie (
    id                  bigserial       primary key,
    title               varchar(50)     not null,
    release_date        date            not null
);
