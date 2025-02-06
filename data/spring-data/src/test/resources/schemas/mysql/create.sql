create table app_user (created_at datetime(6), email varchar(255), fk_role_id varchar(255) not null, id varchar(255) not null, name varchar(255), status varchar(255), primary key (id)) engine=InnoDB;
create table role (id varchar(255) not null, value varchar(255), primary key (id)) engine=InnoDB;
create table versioned_entity (value integer not null, version integer not null, id bigint not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
alter table role add constraint UKktnu5wbb3n2h0kjil0h22xp5e unique (value);
alter table app_user add constraint FK6hk0su96j1pxflfs7kbeklccl foreign key (fk_role_id) references role (id);
