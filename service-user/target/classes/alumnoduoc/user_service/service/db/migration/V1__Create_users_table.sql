-- Migration V1: Create users table
-- This migration creates the users table with the same structure as the original dump

CREATE TABLE IF NOT EXISTS users (
    id_user BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL
);

-- Create index on email for faster lookups (optional but recommended)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Create index on username for faster lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Add comment to table
COMMENT ON TABLE users IS 'Table to store user information';
COMMENT ON COLUMN users.id_user IS 'Primary key - user identifier';
COMMENT ON COLUMN users.email IS 'User email address (unique)';
COMMENT ON COLUMN users.password IS 'User password (should be hashed in production)';
COMMENT ON COLUMN users.username IS 'Username for login';