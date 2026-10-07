CREATE TABLE time_entry (
    id          BIGSERIAL PRIMARY KEY,
    project     VARCHAR(100) NOT NULL,
    start_time  TIMESTAMP    NOT NULL,
    end_time    TIMESTAMP    NOT NULL,
    description VARCHAR(500)
);

CREATE INDEX idx_time_entry_start_time ON time_entry (start_time);
