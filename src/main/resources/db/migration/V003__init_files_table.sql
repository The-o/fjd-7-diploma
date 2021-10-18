CREATE TABLE file (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    name TEXT NOT NULL,
    size INT NOT NULL,
    hash TEXT NULL,

    CONSTRAINT file_user_id_fk
        FOREIGN KEY (user_id) REFERENCES app_user (id)
            ON DELETE CASCADE
            ON UPDATE NO ACTION,

    CONSTRAINT file_user_id_name_unq
        UNIQUE (user_id, name)
);