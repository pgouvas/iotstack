DROP TABLE IF EXISTS node;
DROP TABLE IF EXISTS peer;

CREATE TABLE node
(
   id varchar(40) not null,
   clusterhead varchar(40),
   primary key(id)
);

CREATE TABLE peer
(
   fromnode varchar(40) not null,
   tonode varchar(40) not null,
);