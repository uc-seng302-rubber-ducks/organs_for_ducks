--Remove all existing tables
DROP TABLE IF EXISTS PasswordDetails;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS EmergencyContactDetails;
DROP TABLE IF EXISTS ContactDetails;
DROP TABLE IF EXISTS MedicalProcedure;
DROP TABLE IF EXISTS HealthDetails;
DROP TABLE IF EXISTS MedicationDates;
DROP TABLE IF EXISTS Medication;
DROP TABLE IF EXISTS CurrentDisease;
DROP TABLE IF EXISTS PreviousDisease;
DROP TABLE IF EXISTS BloodType;
DROP TABLE IF EXISTS Organ;
DROP TABLE IF EXISTS Administrator;
DROP TABLE IF EXISTS Clinician;
DROP TABLE IF EXISTS User;

--create all tables needed by the ODMS
CREATE TABLE User(
  nhi varchar(7) PRIMARY KEY,
  firstName VARCHAR(255),
  middleName VARCHAR(255),
  lastName VARCHAR(255),
  preferedName VARCHAR(255),
  timeCreated DATETIME,
  lastModified DATETIME,
  profilePicture BLOB

);

CREATE TABLE Clinician(
  staffId INT PRIMARY KEY,
  firstName VARCHAR(255),
  middleName VARCHAR(255),
  lastName VARCHAR(255),
  timeCreated DATETIME,
  lastModified DATETIME
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

CREATE TABLE BloodType(
  bloodTypeId SMALLINT PRIMARY KEY,
  bloodType VARCHAR(10)
);

CREATE TABLE  PreviousDisease(
  diseaseName VARCHAR(255) NOT NULL,
  diagnosisDate DATETIME not NULL,
  fkUserNhi VARCHAR(7) NOT NULL,
  remissionDate DATETIME,
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

CREATE TABLE HealthDetails(
  fkUserNhi VARCHAR(7) NOT NULL PRIMARY KEY,
  gender VARCHAR(15),
  birthGender VARCHAR(15),
  smoker VARCHAR(255),
  alcoholConsumption VARCHAR(255),
  height DOUBLE,
  weight DOUBLE,
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
  --needs reference to organ added (Maybe using an intermediate table to store many-to-many relationship (many-to-many idiom) ?
);

CREATE TABLE MedicalProcedure(
  procedureName VARCHAR(255) NOT NULL,
  procedureDate DATE NOT NULL,
  fkUserNhi VARCHAR(7) NOT NULL,
  procedureDescription VARCHAR(65000),
  --Needs reference to organs added
  PRIMARY KEY (procedureDate,procedureName,fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE ContactDetails(
  contactId INT AUTO_INCREMENT PRIMARY KEY,
  fkUserNhi VARCHAR(7) UNIQUE,
  fkStaffId INT UNIQUE ,
  homePhone VARCHAR(31),
  cellPhone VARCHAR(31),
  email VARCHAR(255),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE
);

CREATE TABLE EmergencyContactDetails(
  fkContactId INT NOT NULL PRIMARY KEY,
  contactName VARCHAR(255) NOT NULL ,
  contactRelationship VARCHAR(255) NOT NULL,
  FOREIGN KEY (fkContactId) REFERENCES ContactDetails(contactId) ON DELETE CASCADE
);

CREATE TABLE Address(
  fkContactId INT PRIMARY KEY,
  streetNumber INT,
  streetName VARCHAR(255),
  neighbourhood VARCHAR(255),
  city VARCHAR(25),
  region VARCHAR(255),
  country VARCHAR(255),
  fkUserNhi VARCHAR(7),
  fkStaffId INT,
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE,
  FOREIGN KEY (fkContactId) REFERENCES ContactDetails(contactId) ON DELETE CASCADE
);

CREATE TABLE PasswordDetails(
  password_id INT AUTO_INCREMENT PRIMARY KEY,
  fkAdminUserName VARCHAR(255) UNIQUE,
  fkStaffId INT UNIQUE,
  hash BLOB,
  salt BLOB,
  FOREIGN KEY (fkAdminUserName) REFERENCES Administrator(userName) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE
)