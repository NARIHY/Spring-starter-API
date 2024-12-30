CREATE TABLE test_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL
);

INSERT INTO test_entity (name, price)
VALUES ('Test Product 1', 25.99);

INSERT INTO test_entity (name, price)
VALUES ('Test Product 2', 19.49);

INSERT INTO test_entity (name, price)
VALUES ('Test Product 3', 15.75);
