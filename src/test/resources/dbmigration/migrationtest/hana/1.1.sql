-- Migrationscripts for ebean unittest
-- apply changes
create column table migtest_e_user (
  id                            integer generated by default as identity not null,
  constraint pk_migtest_e_user primary key (id)
);

create column table migtest_mtm_c_migtest_mtm_m (
  migtest_mtm_c_id              integer not null,
  migtest_mtm_m_id              bigint not null,
  constraint pk_migtest_mtm_c_migtest_mtm_m primary key (migtest_mtm_c_id,migtest_mtm_m_id)
);

create column table migtest_mtm_m_migtest_mtm_c (
  migtest_mtm_m_id              bigint not null,
  migtest_mtm_c_id              integer not null,
  constraint pk_migtest_mtm_m_migtest_mtm_c primary key (migtest_mtm_m_id,migtest_mtm_c_id)
);

alter table migtest_ckey_detail add ( one_key integer);
alter table migtest_ckey_detail add ( two_key nvarchar(127));

alter table migtest_ckey_detail add constraint fk_migtest_ckey_detail_parent foreign key (one_key,two_key) references migtest_ckey_parent (one_key,two_key) on delete restrict on update restrict;
alter table migtest_ckey_parent add ( assoc_id integer);

alter table migtest_fk_cascade drop constraint  fk_migtest_fk_cascade_one_id;
alter table migtest_fk_cascade add constraint fk_migtest_fk_cascade_one_id foreign key (one_id) references migtest_fk_cascade_one (id) on delete restrict on update restrict;
alter table migtest_fk_none add constraint fk_migtest_fk_none_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;
alter table migtest_fk_none_via_join add constraint fk_migtest_fk_none_via_join_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;
alter table migtest_fk_set_null drop constraint  fk_migtest_fk_set_null_one_id;
alter table migtest_fk_set_null add constraint fk_migtest_fk_set_null_one_id foreign key (one_id) references migtest_fk_one (id) on delete restrict on update restrict;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest2';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint uq_migtest_e_basic_indextest6';
end;
$$;

update migtest_e_basic set status = 'A' where status is null;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint ck_migtest_e_basic_status';
end;
$$;
alter table migtest_e_basic alter ( status nvarchar(1) default 'A' not null);
alter table migtest_e_basic add constraint ck_migtest_e_basic_status check ( status in ('N','A','I','?'));
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_basic drop constraint ck_migtest_e_basic_status2';
end;
$$;
alter table migtest_e_basic alter ( status2 nvarchar(127) default null);

-- rename all collisions;
-- cannot create unique index "uq_migtest_e_basic_description" on table "migtest_e_basic" with nullable columns;

insert into migtest_e_user (id) select distinct user_id from migtest_e_basic;
alter table migtest_e_basic add constraint fk_migtest_e_basic_user_id foreign key (user_id) references migtest_e_user (id) on delete restrict on update restrict;
alter table migtest_e_basic alter ( user_id integer);
alter table migtest_e_basic add ( new_string_field nvarchar(255) default 'foo''bar' not null);
alter table migtest_e_basic add ( new_boolean_field boolean default true not null);
update migtest_e_basic set new_boolean_field = old_boolean;

alter table migtest_e_basic add ( new_boolean_field2 boolean default true not null);
alter table migtest_e_basic add ( progress integer default 0 not null);
alter table migtest_e_basic add constraint ck_migtest_e_basic_progress check ( progress in (0,1,2));
alter table migtest_e_basic add ( new_integer integer default 42 not null);

-- cannot create unique index "uq_migtest_e_basic_status_indextest1" on table "migtest_e_basic" with nullable columns;
-- cannot create unique index "uq_migtest_e_basic_name" on table "migtest_e_basic" with nullable columns;
-- cannot create unique index "uq_migtest_e_basic_indextest4" on table "migtest_e_basic" with nullable columns;
-- cannot create unique index "uq_migtest_e_basic_indextest5" on table "migtest_e_basic" with nullable columns;
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_enum drop constraint ck_migtest_e_enum_test_status';
end;
$$;
comment on column migtest_e_history.test_string is 'Column altered to long now';
alter table migtest_e_history alter ( test_string bigint);
comment on table migtest_e_history is 'We have history now';
alter table migtest_e_history2 drop system versioning /* 0 */;

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history2 set test_string = 'unknown' where test_string is null;
alter table migtest_e_history2 alter ( test_string nvarchar(255) default 'unknown' not null);
alter table migtest_e_history2_history alter ( test_string nvarchar(255) default 'unknown' not null);
alter table migtest_e_history2 add system versioning history table migtest_e_history2_history not validated /* 1 */;
alter table migtest_e_history2 drop system versioning /* 2 */;
alter table migtest_e_history2 add ( test_string2 nvarchar(255));
alter table migtest_e_history2 add ( test_string3 nvarchar(255) default 'unknown' not null);
alter table migtest_e_history2 add ( new_column nvarchar(20));

alter table migtest_e_history2_history add ( test_string2 nvarchar(255));
alter table migtest_e_history2_history add ( test_string3 nvarchar(255) default 'unknown');
alter table migtest_e_history2_history add ( new_column nvarchar(20));
alter table migtest_e_history2 add system versioning history table migtest_e_history2_history not validated /* 3 */;
alter table migtest_e_history3 drop system versioning /* 4 */;
alter table migtest_e_history3 add system versioning history table migtest_e_history3_history not validated /* 5 */;
alter table migtest_e_history4 drop system versioning /* 6 */;
alter table migtest_e_history4 alter ( test_number bigint);
alter table migtest_e_history4_history alter ( test_number bigint);
alter table migtest_e_history4 add system versioning history table migtest_e_history4_history not validated /* 7 */;
alter table migtest_e_history5 drop system versioning /* 8 */;
alter table migtest_e_history5 add ( test_boolean boolean default false not null);

alter table migtest_e_history5_history add ( test_boolean boolean default false);
alter table migtest_e_history5 add system versioning history table migtest_e_history5_history not validated /* 9 */;
alter table migtest_e_history6 drop system versioning /* 10 */;

-- NOTE: table has @History - special migration may be necessary
update migtest_e_history6 set test_number1 = 42 where test_number1 is null;
alter table migtest_e_history6 alter ( test_number1 integer default 42 not null);
alter table migtest_e_history6_history alter ( test_number1 integer default 42 not null);
alter table migtest_e_history6 add system versioning history table migtest_e_history6_history not validated /* 11 */;
alter table migtest_e_history6 drop system versioning /* 12 */;
alter table migtest_e_history6 alter ( test_number2 integer);
alter table migtest_e_history6_history alter ( test_number2 integer);
alter table migtest_e_history6 add system versioning history table migtest_e_history6_history not validated /* 13 */;
alter table migtest_e_index1 alter ( string1 nvarchar(20));
alter table migtest_e_index1 alter ( string2 nvarchar(20));
alter table migtest_e_index2 alter ( string1 nvarchar(20));
alter table migtest_e_index2 alter ( string2 nvarchar(20));
alter table migtest_e_index3 alter ( string1 nvarchar(20));
alter table migtest_e_index3 alter ( string2 nvarchar(20));
alter table migtest_e_index4 alter ( string1 nvarchar(20));
alter table migtest_e_index4 alter ( string2 nvarchar(20));
alter table migtest_e_index5 alter ( string1 nvarchar(20));
alter table migtest_e_index5 alter ( string2 nvarchar(20));
delimiter $$
do
begin
declare exit handler for sql_error_code 397 begin end;
exec 'alter table migtest_e_index6 drop constraint uq_migtest_e_index6_string1';
end;
$$;
alter table migtest_e_index6 alter ( string1 nvarchar(20));
alter table migtest_e_index6 alter ( string2 nvarchar(20));
alter table migtest_e_softdelete add ( deleted boolean default false not null);

alter table migtest_oto_child add ( master_id bigint);

-- explicit index "ix_migtest_e_basic_indextest3" for single column "indextest3" of table "migtest_e_basic" is not necessary;
-- explicit index "ix_migtest_e_basic_indextest6" for single column "indextest6" of table "migtest_e_basic" is not necessary;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_migtest_e_basic_indextest1';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_migtest_e_basic_indextest5';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_migtest_e_index5';
end;
$$;
delimiter $$
do
begin
declare exit handler for sql_error_code 261 begin end;
exec 'drop index ix_migtest_e_index6_string2';
end;
$$;
-- explicit index "ix_migtest_mtm_c_migtest_mtm_m_migtest_mtm_c" for single column "migtest_mtm_c_id" of table "migtest_mtm_c_migtest_mtm_m" is not necessary;
alter table migtest_mtm_c_migtest_mtm_m add constraint fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_c foreign key (migtest_mtm_c_id) references migtest_mtm_c (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_mtm_c_migtest_mtm_m_migtest_mtm_m" for single column "migtest_mtm_m_id" of table "migtest_mtm_c_migtest_mtm_m" is not necessary;
alter table migtest_mtm_c_migtest_mtm_m add constraint fk_migtest_mtm_c_migtest_mtm_m_migtest_mtm_m foreign key (migtest_mtm_m_id) references migtest_mtm_m (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_mtm_m_migtest_mtm_c_migtest_mtm_m" for single column "migtest_mtm_m_id" of table "migtest_mtm_m_migtest_mtm_c" is not necessary;
alter table migtest_mtm_m_migtest_mtm_c add constraint fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_m foreign key (migtest_mtm_m_id) references migtest_mtm_m (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_mtm_m_migtest_mtm_c_migtest_mtm_c" for single column "migtest_mtm_c_id" of table "migtest_mtm_m_migtest_mtm_c" is not necessary;
alter table migtest_mtm_m_migtest_mtm_c add constraint fk_migtest_mtm_m_migtest_mtm_c_migtest_mtm_c foreign key (migtest_mtm_c_id) references migtest_mtm_c (id) on delete restrict on update restrict;

-- explicit index "ix_migtest_ckey_parent_assoc_id" for single column "assoc_id" of table "migtest_ckey_parent" is not necessary;
alter table migtest_ckey_parent add constraint fk_migtest_ckey_parent_assoc_id foreign key (assoc_id) references migtest_ckey_assoc (id) on delete restrict on update restrict;

alter table migtest_oto_child add constraint fk_migtest_oto_child_master_id foreign key (master_id) references migtest_oto_master (id) on delete restrict on update restrict;

create column table migtest_e_history_history (
 id integer,
 test_string bigint,
 sys_period_start timestamp,
 sys_period_end timestamp
);
alter table migtest_e_history add (
    sys_period_start TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW START, 
    sys_period_end TIMESTAMP NOT NULL GENERATED ALWAYS AS ROW END
);
alter table migtest_e_history add period for system_time(sys_period_start,sys_period_end);
alter table migtest_e_history add system versioning history table migtest_e_history_history;
