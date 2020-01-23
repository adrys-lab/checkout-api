package com.adrian.rebollo.types;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL95Dialect;

public class PostgreSQLDialect extends PostgreSQL95Dialect {

    public PostgreSQLDialect() {
        super();
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
