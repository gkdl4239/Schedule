USE schedule;
CREATE TABLE schedule
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    toDo VARCHAR(200) NOT NULL,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    createdDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modifiedDate DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
);

Create table author(
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL,
                       createdAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updatedAt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE schedule
    ADD COLUMN author_id bigint;

ALTER TABLE schedule
    ADD CONSTRAINT fk_author FOREIGN KEY (author_id)
    REFERENCES author(id) ON DELETE CASCADE;

ALTER TABLE schedule
DROP COLUMN name;