CREATE TABLE person (
    id serial PRIMARY key,
    document_type varchar(30) not NULL,
    document_number varchar(50) NOT NULL,
    first_name varchar(50) NOT NULL,
    surname varchar(50) NOT NULL,
    birth_date date,
    created_at timestamp NOT null default now(),
    updated_at timestamp NOT null default now()
);
