create table role (id bigint not null auto_increment, value varchar(255), primary key (id)) engine=InnoDB;
create table user (created_at datetime(6), fk_role_id bigint not null, id bigint not null auto_increment, email varchar(255), name varchar(255), status varchar(255), primary key (id)) engine=InnoDB;
create table versioned_entity (value integer not null, version integer not null, id bigint not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
alter table role add constraint UKktnu5wbb3n2h0kjil0h22xp5e unique (value);
alter table user add constraint FKagw9fsin8811v5n6x4u1wwrlk foreign key (fk_role_id) references role (id);
