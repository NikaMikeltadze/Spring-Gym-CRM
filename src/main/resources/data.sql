-- ===== TRAINING TYPES =====
INSERT INTO trainingtype (id, training_type_name) VALUES (1, 'Fitness');
INSERT INTO trainingtype (id, training_type_name) VALUES (2, 'Yoga');
INSERT INTO trainingtype (id, training_type_name) VALUES (3, 'Zumba');
INSERT INTO trainingtype (id, training_type_name) VALUES (4, 'Stretching');
INSERT INTO trainingtype (id, training_type_name) VALUES (5, 'Resistance');

-- ===== USERS =====
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (1, 'John', 'Smith', 'John.Smith', '$2a$10$MV5y7V/1P4QoBkUfHDWbo.BDg402ZYb01UbJWIPZA7C8B5upV9e76', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (2, 'Jane', 'Doe', 'Jane.Doe', '$2a$10$q/UgiTlGxMXS6X1.fnjRrejtCff1sPmlNZLtlgE23Iv/GWXIR.uya', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (3, 'Emily', 'Davis', 'Emily.Davis', '$2a$10$8Wwh0CKxsWOGtq7tBIrau.eqYKljKe/8.18OG3vn9DH67bIFBsKJW', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (4, 'Michael', 'Johnson', 'Michael.Johnson', '$2a$10$bxh5ztOg0Vc083ai7SFKc.ipL4bxbZxSnaB4ad0SEi5ZhbICq6jNO', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (5, 'Sarah', 'Williams', 'Sarah.Williams', '$2a$10$P9rDbfm5fwjf4dB4lnxkhurJB.CQCxiCNanAuEbBtMKl7Zf8x5iou', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (6, 'John', 'Smith', 'John.Smith1', '$2a$10$3vxb/XAGcgW00IcDRQPGU.q3dfcx0h4Omw9TY0P0sn132/E0GMPPu', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (7, 'Robert', 'Brown', 'Robert.Brown', '$2a$10$ViBaEf08JSWCDwWzY30e1uqtvdB04bTY/6tgtI7XTwK2Eit/pa4SS', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (8, 'Laura', 'Taylor', 'Laura.Taylor', '$2a$10$66jjepfif0UEVUNgz2Obm.HB2YlCm39j9s3S/FkL9PZzeSumniT/i', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (9, 'David', 'Wilson', 'David.Wilson', '$2a$10$RBOaA.XCIgyYvqIsB9D/JeXdZWI2XIVf/bzAiFkyuHieH2japQMI2', true, 0, NULL);
INSERT INTO users (id, first_name, last_name, username, password, is_active, failed_login_attempts, locked_until) VALUES (10, 'Anna', 'Martinez', 'Anna.Martinez', '$2a$10$.tNNSVZQYxG0EzEKw2tC7uk0PbbLscXNWQdbZvE20y67mKzSijunC', true, 0, NULL);

-- ===== TRAINERS =====
-- Trainers: John.Smith(1-Fitness), Jane.Doe(2-Yoga), Emily.Davis(3-Zumba), Michael.Johnson(4-Stretching), Robert.Brown(7-Resistance)
INSERT INTO trainer (id, user_id, specialization) VALUES (1, 1, 1);
INSERT INTO trainer (id, user_id, specialization) VALUES (2, 2, 2);
INSERT INTO trainer (id, user_id, specialization) VALUES (3, 3, 3);
INSERT INTO trainer (id, user_id, specialization) VALUES (4, 4, 4);
INSERT INTO trainer (id, user_id, specialization) VALUES (7, 7, 5);

-- ===== TRAINEES =====
-- Trainees: Sarah.Williams(5), John.Smith1(6), Laura.Taylor(8), David.Wilson(9), Anna.Martinez(10)
INSERT INTO trainee (id, user_id, date_of_birth, address) VALUES (5, 5, '1995-06-15', '123 Main St New York NY');
INSERT INTO trainee (id, user_id, date_of_birth, address) VALUES (6, 6, '1990-03-22', '456 Oak Ave Los Angeles CA');
INSERT INTO trainee (id, user_id, date_of_birth, address) VALUES (8, 8, '1998-11-08', '789 Pine Rd Chicago IL');
INSERT INTO trainee (id, user_id, date_of_birth, address) VALUES (9, 9, '1992-07-30', '321 Elm St Houston TX');
INSERT INTO trainee (id, user_id, date_of_birth, address) VALUES (10, 10, '2000-01-17', '654 Maple Dr Phoenix AZ');

-- ===== TRAININGS =====
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (1, 5, 1, 'Morning Fitness Bootcamp', 1, '2026-03-01', 60);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (2, 5, 2, 'Evening Yoga Session', 2, '2026-03-03', 45);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (3, 6, 3, 'Zumba Dance Cardio', 3, '2026-03-05', 50);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (4, 8, 4, 'Full Body Stretch', 4, '2026-03-07', 30);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (5, 9, 7, 'Resistance Band Workout', 5, '2026-03-10', 55);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (6, 10, 1, 'Core Strength Training', 1, '2026-03-12', 60);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (7, 8, 2, 'Power Yoga Flow', 2, '2026-03-14', 45);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (8, 9, 3, 'Zumba Party', 3, '2026-03-16', 50);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (9, 10, 4, 'Morning Stretch Routine', 4, '2026-03-18', 30);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (10, 5, 7, 'Advanced Resistance Training', 5, '2026-03-20', 55);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (11, 6, 1, 'Weight Training Basics', 1, '2026-03-22', 60);
INSERT INTO training (id, trainee_id, trainer_id, training_name, training_type_id, training_date, training_duration) VALUES (12, 8, 7, 'Resistance Circuit', 5, '2026-03-25', 50);

ALTER TABLE trainee ALTER COLUMN id RESTART WITH 11;
ALTER TABLE trainer ALTER COLUMN id RESTART WITH 11;
ALTER TABLE users ALTER COLUMN id RESTART WITH 11;
ALTER TABLE training ALTER COLUMN id RESTART WITH 13;
ALTER TABLE trainingtype ALTER COLUMN id RESTART WITH 6;
