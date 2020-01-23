
CREATE TABLE IF NOT EXISTS products (
    id         uuid,
    version    integer not null,
    name       VARCHAR(100) not null,
    price      NUMERIC(10,2) not null,
    currency   VARCHAR(3) not null,
    PRIMARY KEY (id)
);