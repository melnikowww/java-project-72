DROP TABLE IF EXISTS urls;

CREATE TABLE urls (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);

DROP TABLE IF EXISTS url_checks;

CREATE TABLE url_checks (
    id SERIAL PRIMARY KEY,
    status_code INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    h1 VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    url_id INT NOT NULL,
    created_at TIMESTAMP
);