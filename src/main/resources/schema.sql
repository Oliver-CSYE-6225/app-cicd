create table if not exists UserTable(id uuid PRIMARY KEY NOT NULL,first_name text,
                       last_name text, password text, username text unique, account_created timestamp,
                       account_updated timestamp, verified boolean);

create table if not exists ImageTable(id uuid PRIMARY KEY NOT NULL,file_name text,
                                     url text, upload_date timestamp, user_id uuid,
                                     FOREIGN KEY (user_id) REFERENCES UserTable(id));