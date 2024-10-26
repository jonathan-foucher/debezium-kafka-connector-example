drop table if exists debezium_heartbeat;
create table debezium_heartbeat (
    heartbeat_time      timestamptz         not null
);

insert into debezium_heartbeat select now();
