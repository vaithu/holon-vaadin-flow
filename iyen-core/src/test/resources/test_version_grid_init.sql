drop table if exists test_version_grid;

create table test_version_grid (
    id bigint primary key,
    name varchar(100),
    version bigint default 0 not null
);

insert into test_version_grid (id, name, version) values (1, 'initial', 0);

