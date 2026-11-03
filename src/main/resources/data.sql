
USE coworking_db;

-- Sample workspace data
INSERT INTO workspaces
(space_name, space_type, hourly_rate, is_active)
VALUES
('Desk-A10', 'HOT_DESK', 50.00, 1),
('Desk-A11', 'HOT_DESK', 50.00, 1),
('Desk-B10', 'DEDICATED_DESK', 100.00, 1),
('Meeting-Room-1', 'MEETING_ROOM', 300.00, 1),
('Cabin-1', 'CABIN', 500.00, 1),
('Desk-C10', 'HOT_DESK', 75.00, 1);

-- Sample user data
INSERT INTO users
(full_name, email, wallet_balance)
VALUES
('Gauri Damare', 'gauri@gmail.com', 2000.00),
('Rahul Patil', 'rahul@gmail.com', 1500.00),
('Priya Sharma', 'priya@gmail.com', 500.00);
