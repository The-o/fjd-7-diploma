CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    login TEXT NOT NULL,
    password TEXT NOT NULL
);

CREATE TABLE session (
    uuid TEXT PRIMARY KEY,
    user_id INT NOT NULL,
    ip TEXT NOT NULL,

    CONSTRAINT session_user_id_fk 
        FOREIGN KEY (user_id) REFERENCES app_user (id) 
            ON DELETE CASCADE
            ON UPDATE NO ACTION
);
