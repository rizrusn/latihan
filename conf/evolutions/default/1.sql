# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table Task (
  id                        bigint not null,
  name                      varchar(255),
  done                      boolean,
  due_date                  timestamp,
  constraint pk_Task primary key (id))
;

create table User (
  username                  varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_User primary key (username))
;

create sequence Task_seq;

create sequence User_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists Task;

drop table if exists User;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists Task_seq;

drop sequence if exists User_seq;

