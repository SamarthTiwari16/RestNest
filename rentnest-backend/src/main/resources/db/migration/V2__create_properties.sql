CREATE TABLE properties (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    locality VARCHAR(100) NOT NULL,
    rent DECIMAL(10, 2) NOT NULL,
    bhk INT NOT NULL,
    property_type VARCHAR(50) NOT NULL,
    furnished BOOLEAN NOT NULL,
    pet_friendly BOOLEAN NOT NULL,
    parking BOOLEAN NOT NULL,
    available_from DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    CONSTRAINT pk_properties PRIMARY KEY (id),
    CONSTRAINT fk_properties_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);
