Модул за Spring Database Property Source
----------------------------------------

Схемата се записва в табица **db_property_source**.
Необходими конфигурации:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<database>
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

database-property-source.schema=<schema>
```

------------------------------------------------------------------------------

```SQL
CREATE TABLE <schema>.db_property_source
(
    property_key character varying(128) COLLATE pg_catalog."default" NOT NULL,
    property_value character varying(128) COLLATE pg_catalog."default",
    CONSTRAINT pk_key PRIMARY KEY (property_key)
)
WITH (
    OIDS = FALSE
)

ALTER TABLE <schema>.db_property_source
    OWNER to postgres;
```