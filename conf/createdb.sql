    DROP DATABASE IF EXISTS readtime;
    CREATE DATABASE readtime default charset utf8 COLLATE utf8_general_ci;
    GRANT ALL PRIVILEGES ON readtime.* TO bus_rw@'localhost' IDENTIFIED BY 'bus_pw';
    GRANT ALL PRIVILEGES ON readtime.* TO bus_rw@'127.0.0.1' IDENTIFIED BY 'bus_pw';