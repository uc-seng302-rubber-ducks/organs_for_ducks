DROP TABLE IF EXISTS DeathDetails;
DROP TABLE IF EXISTS PasswordDetails;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS EmergencyContactDetails;
DROP TABLE IF EXISTS ContactDetails;
DROP TABLE IF EXISTS MedicalProcedureOrgan;
DROP TABLE IF EXISTS MedicalProcedure;
DROP TABLE IF EXISTS HealthDetails;
DROP TABLE IF EXISTS OrganDonatingDates;
DROP TABLE IF EXISTS OrganDonating;
DROP TABLE IF EXISTS OrganAwaitingDates;
DROP TABLE IF EXISTS OrganAwaiting;
DROP TABLE IF EXISTS MedicationDates;
DROP TABLE IF EXISTS Medication;
DROP TABLE IF EXISTS CurrentDisease;
DROP TABLE IF EXISTS PreviousDisease;
DROP TABLE IF EXISTS Organ;
DROP TABLE IF EXISTS Administrator;
DROP TABLE IF EXISTS Clinician;
DROP TABLE IF EXISTS User;


CREATE TABLE User(
  nhi varchar(7) PRIMARY KEY,
  firstName VARCHAR(255),
  middleName VARCHAR(255),
  lastName VARCHAR(255),
  preferedName VARCHAR(255),
  dob DATE,
  dod DATE,
  timeCreated DATETIME,
  lastModified DATETIME,
  profilePicture MEDIUMBLOB,
  pictureFormat VARCHAR(255)
);

CREATE TABLE Clinician(
  staffId VARCHAR(255) PRIMARY KEY,
  firstName VARCHAR(255),
  middleName VARCHAR(255),
  lastName VARCHAR(255),
  timeCreated DATETIME,
  lastModified DATETIME,
  profilePicture MEDIUMBLOB,
  pictureFormat VARCHAR(255)
);

CREATE TABLE Administrator(
  userName VARCHAR(255) PRIMARY KEY,
  firstName VARCHAR(255),
  middleName VARCHAR(255),
  lastName VARCHAR(255),
  timeCreated DATETIME,
  lastModified DATETIME
);

CREATE TABLE Organ(
  organId SMALLINT PRIMARY KEY,
  organName varchar(255)
);

CREATE TABLE  PreviousDisease(
  diseaseName VARCHAR(255) NOT NULL,
  diagnosisDate DATETIME not NULL,
  fkUserNhi VARCHAR(7) NOT NULL,
  PRIMARY KEY (diseaseName, diagnosisDate, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE  CurrentDisease(
  diseaseName VARCHAR(255) NOT NULL,
  diagnosisDate DATETIME not NULL,
  fkUserNhi VARCHAR(7) NOT NULL,
  isChronic BOOLEAN,
  PRIMARY KEY (diseaseName, diagnosisDate, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE Medication(
  medicationName VARCHAR(255) NOT NULL,
  fkUserNhi VARCHAR(7) NOT NULL,
  medicationInstanceId int UNIQUE AUTO_INCREMENT,
  PRIMARY KEY (medicationName, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE MedicationDates(
  keyValue INT AUTO_INCREMENT PRIMARY KEY,
  fkMedicationInstanceId int NOT NULL ,
  dateStartedTaking DATETIME NOT NULL,
  dateStoppedTaking DATETIME,
  FOREIGN KEY (fkMedicationInstanceId) REFERENCES Medication(medicationInstanceId) ON DELETE CASCADE
);

CREATE TABLE OrganAwaiting(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  awaitingId INT AUTO_INCREMENT UNIQUE,
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
);

CREATE TABLE OrganAwaitingDates(
  awaitingDateId INT AUTO_INCREMENT PRIMARY KEY,
  dateRegistered DATE,
  dateDeregistered DATE,
  fkAwaitingId INT,
  FOREIGN KEY (fkAwaitingId) REFERENCES OrganAwaiting(awaitingId) ON DELETE CASCADE
);

CREATE TABLE OrganDonating(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  donatingId INT AUTO_INCREMENT UNIQUE,
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
);

CREATE TABLE OrganDonatingDates(
  donatingDateId INT AUTO_INCREMENT PRIMARY KEY,
  dateRegistered DATE,
  dateDeregistered DATE,
  fkAwaitingId INT,
  FOREIGN KEY (fkAwaitingId) REFERENCES OrganDonating(donatingId) ON DELETE CASCADE
);



CREATE TABLE HealthDetails(
  fkUserNhi VARCHAR(7) NOT NULL PRIMARY KEY,
  gender VARCHAR(15),
  birthGender VARCHAR(15),
  smoker BOOLEAN,
  alcoholConsumption VARCHAR(255),
  height DOUBLE,
  weight DOUBLE,
  bloodType VARCHAR(3),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE MedicalProcedure(
  procedureId          INT UNIQUE AUTO_INCREMENT,
  procedureName        VARCHAR(255) NOT NULL,
  procedureDate        DATE NOT NULL,
  fkUserNhi            VARCHAR(7) NOT NULL,
  procedureDescription TEXT,
  PRIMARY KEY (procedureDate,procedureName,fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE MedicalProcedureOrgan (
  fkOrgansId    SMALLINT,
  fkProcedureId INT,
  PRIMARY KEY (fkOrgansId, fkProcedureId),
  FOREIGN KEY (fkProcedureId) REFERENCES MedicalProcedure (procedureId)
    ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ (organId)
    ON DELETE CASCADE
);

CREATE TABLE ContactDetails(
  contactId INT AUTO_INCREMENT PRIMARY KEY,
  fkUserNhi VARCHAR(7),
  fkStaffId VARCHAR(255) UNIQUE,
  homePhone VARCHAR(31),
  cellPhone VARCHAR(31),
  email VARCHAR(255),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE
);

CREATE TABLE EmergencyContactDetails(
  contactName VARCHAR(255) NOT NULL ,
  contactRelationship VARCHAR(255) NOT NULL,
  fkContactId INT NOT NULL,
  fkUserNhi VARCHAR(7) NOT NULL UNIQUE,
  PRIMARY KEY (fkContactId, fkUserNhi),
  FOREIGN KEY (fkContactId) REFERENCES  ContactDetails(contactId) ON DELETE CASCADE,
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE Address(
  fkContactId INT PRIMARY KEY,
  streetNumber VARCHAR(15),
  streetName VARCHAR(255),
  neighbourhood VARCHAR(255),
  city VARCHAR(255),
  region VARCHAR(255),
  zipCode VARCHAR(15),
  country VARCHAR(255),
  fkUserNhi VARCHAR(7),
  fkStaffId VARCHAR(255) UNIQUE,
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE,
  FOREIGN KEY (fkContactId) REFERENCES ContactDetails(contactId) ON DELETE CASCADE
);

CREATE TABLE PasswordDetails(
  password_id INT AUTO_INCREMENT PRIMARY KEY,
  fkAdminUserName VARCHAR(255) UNIQUE,
  fkStaffId VARCHAR(255) UNIQUE,
  hash TEXT,
  salt TEXT,
  FOREIGN KEY (fkAdminUserName) REFERENCES Administrator(userName) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE
);

/*CREATE TABLE Countries(
  countryName VARCHAR(100) PRIMARY KEY,
  allowed BOOLEAN DEFAULT TRUE
);*/

CREATE TABLE DeathDetails(
  fkUserNhi VARCHAR(7) NOT NULL PRIMARY KEY,
  momentOfDeath DATETIME,
  city VARCHAR(255),
  region VARCHAR(255),
  country VARCHAR(255),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);
