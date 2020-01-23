
CREATE TABLE IF NOT EXISTS orders (
    id                      uuid,
    version                 integer not null,
    placed_date             timestamp without time zone not null,
    price                   NUMERIC (10, 2) not null,
    currency                VARCHAR(3) not null,
    product_list            jsonb not null,
    PRIMARY KEY (id)
);
