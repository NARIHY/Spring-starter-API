CREATE TABLE test_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL,
    creation_date datetime not null,
    last_modified_date datetime not null
);

