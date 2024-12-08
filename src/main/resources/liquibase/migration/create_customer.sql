--liquibase formatted sql

--changeset mabhinav:customer.sql runInTransaction:true

CREATE TABLE IF NOT EXISTS customer(
    id                          SERIAL PRIMARY KEY,
    name                        character varying,
    email                       character varying,
    contact_number              character varying,
    is_delete                   BOOLEAN,
    updated_by                  BIGINT,
    ip_address                  character varying,
    created_at                  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMP NOT NULL DEFAULT NOW()
);

