-- Migrationscripts for ebean unittest
-- apply changes
create table migtest_ckey_assoc (
  id                            integer generated by default as identity not null,
  assoc_one                     varchar(255),
  constraint pk_migtest_ckey_assoc primary key (id)
);

create table migtest_ckey_detail (
  id                            integer generated by default as identity not null,
  something                     varchar(255),
  constraint pk_migtest_ckey_detail primary key (id)
);

create table migtest_ckey_parent (
  one_key                       integer not null,
  two_key                       varchar(127) not null,
  name                          varchar(255),
  version                       integer not null,
  constraint pk_migtest_ckey_parent primary key (one_key,two_key)
);

create table migtest_fk_cascade (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_cascade primary key (id)
);

create table migtest_fk_cascade_one (
  id                            bigint generated by default as identity not null,
  constraint pk_migtest_fk_cascade_one primary key (id)
);

create table migtest_fk_none (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none primary key (id)
);

create table migtest_fk_none_via_join (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_none_via_join primary key (id)
);

create table migtest_fk_one (
  id                            bigint generated by default as identity not null,
  constraint pk_migtest_fk_one primary key (id)
);

create table migtest_fk_set_null (
  id                            bigint generated by default as identity not null,
  one_id                        bigint,
  constraint pk_migtest_fk_set_null primary key (id)
);

create table migtest_e_basic (
  id                            integer generated by default as identity not null,
  status                        varchar(1),
  status2                       varchar(1) default 'N' not null,
  name                          varchar(127),
  description                   varchar(127),
  some_date                     timestamp,
  old_boolean                   boolean default false not null,
  old_boolean2                  boolean,
  eref_id                       integer,
  indextest1                    varchar(127),
  indextest2                    varchar(127),
  indextest3                    varchar(127),
  indextest4                    varchar(127),
  indextest5                    varchar(127),
  indextest6                    varchar(127),
  user_id                       integer not null,
  constraint ck_mgtst__bsc_stts check ( status in ('N','A','I')),
  constraint ck_mgtst__b_z543fg check ( status2 in ('N','A','I')),
  constraint pk_migtest_e_basic primary key (id)
);
-- NOT SUPPORTED alter table migtest_e_basic add constraint uq_mgtst__b_4aybzy unique  (indextest2);
-- NOT SUPPORTED alter table migtest_e_basic add constraint uq_mgtst__b_4ayc02 unique  (indextest6);

create table migtest_e_enum (
  id                            integer generated by default as identity not null,
  test_status                   varchar(1),
  constraint ck_mgtst__n_773sok check ( test_status in ('N','A','I')),
  constraint pk_migtest_e_enum primary key (id)
);

create table migtest_e_history (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_history primary key (id)
);

create table migtest_e_history2 (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  obsolete_string1              varchar(255),
  obsolete_string2              varchar(255),
  constraint pk_migtest_e_history2 primary key (id)
);

create table migtest_e_history3 (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_history3 primary key (id)
);

create table migtest_e_history4 (
  id                            integer generated by default as identity not null,
  test_number                   integer,
  constraint pk_migtest_e_history4 primary key (id)
);

create table migtest_e_history5 (
  id                            integer generated by default as identity not null,
  test_number                   integer,
  constraint pk_migtest_e_history5 primary key (id)
);

create table migtest_e_history6 (
  id                            integer generated by default as identity not null,
  test_number1                  integer,
  test_number2                  integer not null,
  constraint pk_migtest_e_history6 primary key (id)
);

create table migtest_e_index1 (
  id                            integer generated by default as identity not null,
  string1                       varchar(10),
  string2                       varchar(10),
  constraint pk_migtest_e_index1 primary key (id)
);

create table migtest_e_index2 (
  id                            integer generated by default as identity not null,
  string1                       varchar(10),
  string2                       varchar(10),
  constraint pk_migtest_e_index2 primary key (id)
);
-- NOT SUPPORTED alter table migtest_e_index2 add constraint uq_mgtst__ndx2 unique  (string1,string2);

create table migtest_e_index3 (
  id                            integer generated by default as identity not null,
  string1                       varchar(10),
  string2                       varchar(10),
  constraint pk_migtest_e_index3 primary key (id)
);
-- NOT SUPPORTED alter table migtest_e_index3 add constraint uq_mgtst__ndx3 unique  (string1);

create table migtest_e_index4 (
  id                            integer generated by default as identity not null,
  string1                       varchar(10),
  string2                       varchar(10),
  constraint pk_migtest_e_index4 primary key (id)
);
-- NOT SUPPORTED alter table migtest_e_index4 add constraint uq_mgtst__n_pkct5u unique  (string1);

create table migtest_e_index5 (
  id                            integer generated by default as identity not null,
  string1                       varchar(10),
  string2                       varchar(10),
  constraint pk_migtest_e_index5 primary key (id)
);

create table migtest_e_index6 (
  id                            integer generated by default as identity not null,
  string1                       varchar(10),
  string2                       varchar(10),
  constraint pk_migtest_e_index6 primary key (id)
);
-- NOT SUPPORTED alter table migtest_e_index6 add constraint uq_mgtst__n_1aoskk unique  (string1);

create table migtest_e_ref (
  id                            integer generated by default as identity not null,
  name                          varchar(127) not null,
  constraint pk_migtest_e_ref primary key (id)
);
alter table migtest_e_ref add constraint uq_mgtst__rf_nm unique  (name);

create table migtest_e_softdelete (
  id                            integer generated by default as identity not null,
  test_string                   varchar(255),
  constraint pk_migtest_e_softdelete primary key (id)
);

create table migtest_mtm_c (
  id                            integer generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_c primary key (id)
);

create table migtest_mtm_m (
  id                            bigint generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_mtm_m primary key (id)
);

create table migtest_oto_child (
  id                            integer generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_oto_child primary key (id)
);

create table migtest_oto_master (
  id                            bigint generated by default as identity not null,
  name                          varchar(255),
  constraint pk_migtest_oto_master primary key (id)
);

create index ix_mgtst__b_eu8csq on migtest_e_basic (indextest1);
create index ix_mgtst__b_eu8csu on migtest_e_basic (indextest5);
create index ix_mgtst__ndx1 on migtest_e_index1 (string1,string2);
create index ix_mgtst__ndx3 on migtest_e_index3 (string2);
create index ix_mgtst__n_fw69v4 on migtest_e_index4 (string2);
create index ix_mgtst__ndx5 on migtest_e_index5 (string1,string2);
create index ix_mgtst__n_r52a9e on migtest_e_index6 (string2);
create index ix_mgtst_fk_mok1xj on migtest_fk_cascade (one_id);
alter table migtest_fk_cascade add constraint fk_mgtst_fk_65kf6l foreign key (one_id) references migtest_fk_cascade_one (id) on delete cascade;

create index ix_mgtst_fk_c4p3mv on migtest_fk_set_null (one_id);
alter table migtest_fk_set_null add constraint fk_mgtst_fk_wicx8x foreign key (one_id) references migtest_fk_one (id) on delete set null;

create index ix_mgtst__bsc_rf_d on migtest_e_basic (eref_id);
alter table migtest_e_basic add constraint fk_mgtst__bsc_rf_d foreign key (eref_id) references migtest_e_ref (id) on delete restrict;

