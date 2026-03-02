-- ===== TRAINING TYPES =====
INSERT INTO trainingtype (id, "Training Type Name") VALUES (1, 'Fitness');
INSERT INTO trainingtype (id, "Training Type Name") VALUES (2, 'Yoga');
INSERT INTO trainingtype (id, "Training Type Name") VALUES (3, 'Zumba');
INSERT INTO trainingtype (id, "Training Type Name") VALUES (4, 'Stretching');
INSERT INTO trainingtype (id, "Training Type Name") VALUES (5, 'Resistance');

-- ===== USERS =====
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (1, 'John', 'Smith', 'John.Smith', 'aB3dEfGh1K', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (2, 'Jane', 'Doe', 'Jane.Doe', 'xR7mNpQ2sW', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (3, 'Emily', 'Davis', 'Emily.Davis', 'kL9wYzA4cV', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (4, 'Michael', 'Johnson', 'Michael.Johnson', 'tU6vHjD8eX', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (5, 'Sarah', 'Williams', 'Sarah.Williams', 'qF5nBrM3gZ', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (6, 'John', 'Smith', 'John.Smith1', 'pO2iYlC7wN', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (7, 'Robert', 'Brown', 'Robert.Brown', 'hJ4sXdK9mQ', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (8, 'Laura', 'Taylor', 'Laura.Taylor', 'bV8fRgE1nA', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (9, 'David', 'Wilson', 'David.Wilson', 'cW3kPzL6tY', true);
INSERT INTO users (id, "First Name", "Last Name", Username, Password, IsActive) VALUES (10, 'Anna', 'Martinez', 'Anna.Martinez', 'dX7jQhN0uB', true);

-- ===== TRAINERS =====
-- Trainers: John.Smith(1-Fitness), Jane.Doe(2-Yoga), Emily.Davis(3-Zumba), Michael.Johnson(4-Stretching), Robert.Brown(7-Resistance)
INSERT INTO trainer (id, "User Id", Specialization) VALUES (1, 1, 1);
INSERT INTO trainer (id, "User Id", Specialization) VALUES (2, 2, 2);
INSERT INTO trainer (id, "User Id", Specialization) VALUES (3, 3, 3);
INSERT INTO trainer (id, "User Id", Specialization) VALUES (4, 4, 4);
INSERT INTO trainer (id, "User Id", Specialization) VALUES (7, 7, 5);

-- ===== TRAINEES =====
-- Trainees: Sarah.Williams(5), John.Smith1(6), Laura.Taylor(8), David.Wilson(9), Anna.Martinez(10)
INSERT INTO trainee (id, UserId, "Date of Birth", Address) VALUES (5, 5, '1995-06-15', '123 Main St New York NY');
INSERT INTO trainee (id, UserId, "Date of Birth", Address) VALUES (6, 6, '1990-03-22', '456 Oak Ave Los Angeles CA');
INSERT INTO trainee (id, UserId, "Date of Birth", Address) VALUES (8, 8, '1998-11-08', '789 Pine Rd Chicago IL');
INSERT INTO trainee (id, UserId, "Date of Birth", Address) VALUES (9, 9, '1992-07-30', '321 Elm St Houston TX');
INSERT INTO trainee (id, UserId, "Date of Birth", Address) VALUES (10, 10, '2000-01-17', '654 Maple Dr Phoenix AZ');

-- ===== TRAININGS =====
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (1, 5, 1, 'Morning Fitness Bootcamp', 1, '2026-03-01', 60);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (2, 5, 2, 'Evening Yoga Session', 2, '2026-03-03', 45);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (3, 6, 3, 'Zumba Dance Cardio', 3, '2026-03-05', 50);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (4, 8, 4, 'Full Body Stretch', 4, '2026-03-07', 30);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (5, 9, 7, 'Resistance Band Workout', 5, '2026-03-10', 55);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (6, 10, 1, 'Core Strength Training', 1, '2026-03-12', 60);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (7, 8, 2, 'Power Yoga Flow', 2, '2026-03-14', 45);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (8, 9, 3, 'Zumba Party', 3, '2026-03-16', 50);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (9, 10, 4, 'Morning Stretch Routine', 4, '2026-03-18', 30);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (10, 5, 7, 'Advanced Resistance Training', 5, '2026-03-20', 55);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (11, 6, 1, 'Weight Training Basics', 1, '2026-03-22', 60);
INSERT INTO training (id, "Trainee Id", "Trainer Id", "Training Name", "Training Type Id", "Training Date", "Training Duration") VALUES (12, 8, 7, 'Resistance Circuit', 5, '2026-03-25', 50);
