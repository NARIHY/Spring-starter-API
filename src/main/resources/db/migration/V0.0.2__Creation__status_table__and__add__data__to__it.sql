CREATE TABLE status_entity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(255) NOT NULL,
    creation_date DATETIME NOT NULL,
    last_modified_date DATETIME NOT NULL
);

INSERT INTO status_entity (status_name, creation_date, last_modified_date)
VALUES
    ('Active', '2025-01-15 10:30:00', '2025-01-15 10:30:00'),
    ('Inactive', '2025-01-15 10:30:00', '2025-01-15 10:30:00'),
    ('En cours', '2025-01-15 10:30:00', '2025-01-15 10:30:00'),
    ('Pending', '2025-01-15 10:30:00', '2025-01-15 10:30:00'),
   ('Cancel', '2025-01-15 10:30:00', '2025-01-15 10:30:00');
