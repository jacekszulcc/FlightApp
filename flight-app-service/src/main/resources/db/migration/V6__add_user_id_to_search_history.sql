ALTER TABLE search_history
    ADD COLUMN user_id BIGINT;

ALTER TABLE search_history
    ADD CONSTRAINT fk_search_history_on_user
        FOREIGN KEY (user_id) REFERENCES users (id);