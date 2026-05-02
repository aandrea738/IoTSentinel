CREATE DATABASE iotsentinel_db;
CREATE USER sentinel_user WITH PASSWORD 'password_super_sicura';
GRANT ALL PRIVILEGES ON DATABASE iotsentinel_db TO sentinel_user;

\connect iotsentinel_db

GRANT ALL ON SCHEMA public TO sentinel_user;
