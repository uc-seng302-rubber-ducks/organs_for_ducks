INSERT INTO User(nhi, firstName, middleName, lastName, preferedName, dob, dod, timeCreated, lastModified) VALUES
  ('ABC1234','Allan','Danny Zurich','Levi','Al','1997-01-01', NULL, '2015-06-01 00:01:01','2017-09-07 13:01:01'),
  ('DEF2314', 'Mark', 'Jimmyost', NULL, 'Mark', '1995-01-01', NULL, '2017-01-01 16:15:01', '2018-01-05 17:01:01'),
  ('XYZ1234', 'Lily', 'Ruby Rose', 'Latifer', 'Li', '1987-01-01', NULL, '2013-11-04 18:12:43', '2018-05-05 13:14:32'),
  ('XYZ4321', 'Kenny', NULL, 'Roger', NULL, '1987-01-01', '2017-06-15', '2013-11-04 18:12:43', '2017-06-15 13:14:32');

INSERT INTO Clinician(staffId, firstName, middleName, lastName, timeCreated, lastModified) VALUES
  ('16','Remi','Fabinus','Rulie','1983-10-04 20:12:43','1984-01-05 01:14:32' ),
  ('23','Alex','King','Stone','1988-10-04 15:12:43','1988-12-05 14:14:32');

INSERT INTO Administrator(userName, firstName, middleName, lastName, timeCreated, lastModified) VALUES
  ('ruth265','Remi','Fabinus','Rulie','1983-10-04 20:12:43','1984-01-05 01:14:32'),
  ('fts34','Alex','King','Stone','1988-10-04 15:12:43','1988-12-05 14:14:32');

INSERT INTO Organ(organId, organName) VALUES
  (1,'LIVER'),
  (2, 'KIDNEY'),
  (3, 'PANCREAS'),
  (4,'HEART'),
  (5,'LUNG'),
  (6,'INTESTINE'),
  (7,'CORNEA'),
  (8,'MIDDLE_EAR'),
  (9,'SKIN'),
  (10,'BONE_MARROW'),
  (11,'BONE'),
  (12,'CONNECTIVE_TISSUE');

INSERT INTO PreviousDisease(diseaseName, diagnosisDate, fkUserNhi) VALUES
  ('Chicken Pox', '2015-10-04 19:12:43', 'ABC1234'),
  ('Fever', '2017-11-04 15:12:43', 'ABC1234'),
  ('Malaria', '2014-09-23 18:12:21', 'DEF2314');

INSERT INTO CurrentDisease(diseaseName, diagnosisDate, fkUserNhi, isChronic) VALUES
  ('Lung Cancer', '2018-03-09 18:23:12', 'XYZ1234', TRUE),
  ('Common Cold', '2018-05-13 14:27:15', 'DEF2314', FALSE);

INSERT INTO Medication(medicationName, fkUserNhi, medicationInstanceId) VALUES
  ('Paracetamol', 'ABC1234', 12),
  ('Codeine', 'ABC1234', 15),
  ('Nicotine', 'XYZ1234', 20),
  ('Nicotine', 'DEF2314', 78);

INSERT INTO MedicationDates (keyValue, fkMedicationInstanceId, dateStartedTaking, dateStoppedTaking) VALUES
  (1, 12, '2017-01-01 00:01:01', '2017-06-07 00:01:01'),
  (2, 15, '2018-01-01 00:01:01', NULL),
  (3, 20, '2018-05-06 00:01:01', NULL),
  (4, 78, '2016-02-01 00:01:01', '2016-03-17 00:01:01'),
  (5, 12, '2018-05-05 00:01:01', NULL);

INSERT INTO OrganAwaiting(fkOrgansId, fkUserNhi, awaitingId) VALUES
  (3, 'DEF2314', 1),
  (8, 'DEF2314', 2),
  (8, 'XYZ1234', 3);

INSERT INTO OrganAwaitingDates(awaitingDateId, dateRegistered, dateDeregistered, fkAwaitingId) VALUES
  (1, '2016-11-01', NULL, 1),
  (2, '2016-11-01', '2017-05-06', 2),
  (3, '2016-10-09', NULL, 3),
  (4, '2018-02-01', NULL, 2);

INSERT INTO OrganDonating(fkOrgansId, fkUserNhi, donatingId) VALUES
  (3, 'ABC1234', 1),
  (8, 'ABC1234', 2),
  (5, 'ABC1234', 3);

INSERT INTO OrganDonatingDates(donatingDateId, dateRegistered, dateDeregistered, fkAwaitingId) VALUES
  (1, '2017-02-01', NULL, 1),
  (2, '2017-02-01', NULL, 2),
  (3, '2017-02-01', '2018-01-01', 3);


INSERT INTO HealthDetails(fkUserNhi, gender, birthGender, smoker, alcoholConsumption, height, weight, bloodType) VALUES
  ('ABC1234', 'Male', 'Male', TRUE , 'High', 163.7, 65.8, 'A+'),
  ('DEF2314', 'Male', 'Male', TRUE , 'None', 173.0, 78.8, 'B-'),
  ('XYZ1234', 'Female', 'Female', FALSE, 'Low', 165.4, 54.3, 'AB+');

INSERT INTO MedicalProcedure (procedureId, procedureName, procedureDate, fkUserNhi, procedureDescription) VALUES
  (1, 'Extract Pancreas and Middle Ear', '2018-09-01', 'ABC1234', 'Get Pancreas and Middle Ear from Donor ABC234'),
  (2, 'Implant Pancreas', '2018-10-03', 'DEF2314', 'Implant Pancreas to Receiver DEF2314');

INSERT INTO MedicalProcedureOrgan (fkOrgansId, fkProcedureId) VALUES
  (3, 1),
  (8, 1),
  (3, 2);

INSERT INTO ContactDetails(fkUserNhi, fkStaffId, homePhone, cellPhone, email) VALUES
  ('ABC1234', NULL, NULL,'0221453566', 'aaronB@gmail.com'),
  ('DEF2314', NULL, '094385522','0221453566', 'darwin@yahoo.com'),
  (NULL, '16', '044536474','0234267413', 'remi@hotmail.com'),
  (NULL, '23', '043841212','0221453566', 'lily.rose@hotmail.com'),
  ('ABC1234', NULL, '033338061','0225416653', 'email@gmail.com'),
  ('DEF2314', NULL, '092255834','0226653541', 'email2@yahoo.com');

INSERT INTO EmergencyContactDetails(fkContactId, contactName, contactRelationship, fkUserNhi) VALUES
  (1, 'Julius Ranger', 'Father', 'ABC1234'),
  (2, 'Amy Hampson', 'Cousin', 'DEF2314');

INSERT INTO Address(fkContactId, streetNumber, streetName, neighbourhood, city, region, zipCode, country, fkUserNhi, fkStaffId) VALUES
  (1,'23B', 'Cambridge St', 'Shirley', 'Christchurch', 'Canterbury', '8041', 'New Zealand', 'ABC1234', NULL),
  (5,'43', 'Josh St', 'Latimer', 'Adelaide', 'South Australia', '4336', 'Australia', 'ABC1234', NULL),
  (2, '106A', 'Oxford St', 'Hornby', 'Tauranga', 'Bay of Plenty', '8042', 'New Zealand', 'DEF2314', NULL),
  (6, 'B2', 'Matariki St', 'Papanui', 'Christchurch', 'Canterbury', '8056', 'New Zealand', 'DEF2314', NULL),
  (3, '45', 'Kirkwood Ave', 'Ilam', 'Christchurch', 'Canterbury', '8061', 'New Zealand', NULL, '16'),
  (4, '34', 'Deans  Ave', 'Haswell', 'Christchurch', 'Canterbury', '8032', 'New Zealand', NULL, '23');


INSERT INTO PasswordDetails(password_id, fkAdminUserName, fkStaffId, hash, salt) VALUES
  (1, 'ruth265', NULL, 'DNRVh97GMTUhGWnv8aobdTjPW9vvQBqXA46oEgLLOu8',
   '[-89, 50, 52, 79, 109, 49, -122, 1, -109, -113, -105, -9, 121, 13, -65, 94, 89, 67, 62, -31, 54, -127, 61, -85, 4
   , 96, -126, 39, 57, -113, -68, 88]'),
  (2, 'fts34', NULL, '_HgR_4a1ZsniKwfzTQGcAvmGGwFJp8rZ5CGo-nrgbGw',
   '[-10, -70, 97, 89, 81, 64, -48, 32, 45, -90, -76, -79, 51, 81, -88, -22, -119, 125, -90, 84, 18, 116, -34,
    -119, -81, -100, 62, 96, 81, -34, 27, 75]'),
  (3, NULL, '16', 'uKFPQVlE4awK465dzxynFGAapt-2Np_6u9XAlBYyWsI', '[-84, -6, 43, 63
  , 105, -10, -15, 100, -126, -13, 22, 9, -77, -48, -46, 39, 22, -2, 111, 119, 73, -17, 120, -122, 110, -113, -59,
   96, -51, 43, 40, -105]'),
  (4, NULL, '23', 'qEm6pUtZyUc70w_goHbGUoHSSA4IDdM7vgtbYdzigLo', '[54, -71, 20,
  -65, -104, 56, -85, 73, -74, 123, -87, -13, 40, 9, 3, 44, -64, -105, 120, 47, -23, -63, 31, 51, 49, 23, -77,
  -10, 60, -29, -120, 117]');

INSERT INTO DeathDetails(fkUserNhi, momentOfDeath, city, region, country) VALUES
  ('ABC1234', '2018-08-05 03:22','Christchurch','Canterbury','New Zealand'),
  ('XYZ4321', '1988-01-02 18:20','Singapore', "",'Singapore');