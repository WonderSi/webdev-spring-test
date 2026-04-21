CREATE TABLE dishes
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255)   NOT NULL UNIQUE,
    description   TEXT           NOT NULL,
    price         DECIMAL(10, 2) NOT NULL,
    is_available  BOOLEAN        NOT NULL DEFAULT TRUE,
    restaurant_id BIGINT         NOT NULL,
    CONSTRAINT fk_dishes_restaurant
        FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
);