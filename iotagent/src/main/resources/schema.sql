DROP TABLE IF EXISTS node;
DROP TABLE IF EXISTS peer;
DROP TABLE IF EXISTS commandlog;

CREATE TABLE node
(
   id varchar(40) not null,
   gateway varchar(40),
   osarch varchar(40),
   osname varchar(40),
   bootdate timestamp, 
   username varchar(40),
   password varchar(40),
   vcpus integer,
   cpuspeed integer,
   totalmemory integer,
   primary key(id)
);

CREATE TABLE peer
(
   fromnode varchar(40) not null,
   tonode varchar(40) not null,
   registrationdate timestamp,
   reposrtingnode varchar(40)
);


CREATE TABLE commandlog
(
   cid varchar(40) not null,
   cdate timestamp, 
   primary key(cid)
);