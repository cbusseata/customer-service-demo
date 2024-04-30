CREATE SCHEMA AUTHORIZATION system
    CREATE TABLE customer (
        customerId CHAR(15) PRIMARY KEY,
        firstName VARCHAR2(32) NOT NULL,
        lastName VARCHAR2(32) NOT NULL,
        company VARCHAR2(255) NOT NULL,
        city VARCHAR2(255) NOT NULL,
        country VARCHAR2(255) NOT NULL,
        phone1 VARCHAR2(32) NOT NULL,
        phone2 VARCHAR2(32) NOT NULL,
        email VARCHAR2(255) NOT NULL,
        subscriptionDate DATE DEFAULT sysdate NOT NULL,
        website VARCHAR2(255) NOT NULL
    );
