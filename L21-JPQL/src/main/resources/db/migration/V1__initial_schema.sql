create table test
(
    id   int,
    name varchar(50)
);

create table tPerson
(
    id bigserial primary key,
    address varchar(255),
    createdon timestamp,
    name varchar(255),
    nickname varchar(255)
);
create sequence personId start 1 NO MAXVALUE;

create table tPhone
(
    id bigserial primary key,
    phone_number varchar(255) not null,
    person_id bigint not null references tPerson(id)
);
create sequence phoneId start 1 NO MAXVALUE;