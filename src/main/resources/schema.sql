
CREATE DATABASE IF NOT EXISTS coworking_db;
USE coworking_db;

CREATE TABLE IF NOT EXISTS workspaces (
    space_id INT NOT NULL AUTO_INCREMENT,
    space_name VARCHAR(100) NOT NULL,
    space_type VARCHAR(50) NOT NULL,
    hourly_rate DECIMAL(8,2) NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    PRIMARY KEY (space_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    wallet_balance DECIMAL(10,2) DEFAULT 0.00,
    PRIMARY KEY (user_id),
    UNIQUE KEY email (email)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id INT NOT NULL AUTO_INCREMENT,
    user_id INT NOT NULL,
    space_id INT NOT NULL,
    booking_date DATE NOT NULL,
    slot_hour INT NOT NULL,
    total_cost DECIMAL(8,2) NOT NULL,
    booking_status VARCHAR(30) NOT NULL DEFAULT 'CONFIRMED',
    active_slot TINYINT
        GENERATED ALWAYS AS (
            CASE
                WHEN booking_status = 'CONFIRMED' THEN 1
                ELSE NULL
            END
        ) STORED,

    PRIMARY KEY (booking_id),

    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    CONSTRAINT fk_booking_workspace
        FOREIGN KEY (space_id) REFERENCES workspaces(space_id),

    CONSTRAINT chk_slot_hour
        CHECK (slot_hour BETWEEN 0 AND 23),

    UNIQUE KEY unique_active_booking
        (space_id, booking_date, slot_hour, active_slot)
);
