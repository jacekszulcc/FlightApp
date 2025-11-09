ALTER TABLE favorite_flight
    ADD COLUMN user_id BIGINT;

ALTER TABLE favorite_flight
    ADD CONSTRAINT fk_favorite_flight_on_user
        FOREIGN KEY (user_id) REFERENCES users (id);