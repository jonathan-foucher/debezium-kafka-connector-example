drop table if exists job;
create table job (
    id              bigserial       primary key,
    name            varchar(30)     not null
);

drop index if exists idx01_job;
create index idx01_job on job(name);
