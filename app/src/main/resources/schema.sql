DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);

DROP TABLE IF EXISTS url_checks;

CREATE TABLE url_checks (
    id INT PRIMARY KEY AUTO_INCREMENT,
    status_code INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    h1 VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    url_id INT NOT NULL,
    created_at TIMESTAMP
);