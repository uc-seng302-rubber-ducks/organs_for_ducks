DROP TABLE IF EXISTS PasswordDetails;
DROP TABLE IF EXISTS Address;
DROP TABLE IF EXISTS EmergencyContactDetails;
DROP TABLE IF EXISTS ContactDetails;
DROP TABLE IF EXISTS MedicalProcedure;
DROP TABLE IF EXISTS MedicalProcedureOrgan;
DROP TABLE IF EXISTS HealthDetails;
DROP TABLE IF EXISTS HealthOrganDonate;
DROP TABLE IF EXISTS OrganAwaitingDeRegisterDate;
DROP TABLE IF EXISTS OrganAwaitingRegisterDate;
DROP TABLE IF EXISTS OrganDonatingDeRegisterDate;
DROP TABLE IF EXISTS OrganDonatingRegisterDate;
DROP TABLE IF EXISTS OrganDonating;
DROP TABLE IF EXISTS OrganAwaiting;
DROP TABLE IF EXISTS HealthOrganReceive;
DROP TABLE IF EXISTS MedicationDates;
DROP TABLE IF EXISTS Medication;
DROP TABLE IF EXISTS CurrentDisease;
DROP TABLE IF EXISTS PreviousDisease;
DROP TABLE IF EXISTS BloodType;
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
  profilePicture BLOB
);

CREATE TABLE Clinician(
  staffId VARCHAR(255) PRIMARY KEY,
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

CREATE TABLE HealthOrganReceive(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
);



CREATE TABLE OrganAwaiting(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  awaitingingId INT AUTO_INCREMENT UNIQUE,
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
);

CREATE TABLE OrganDonating(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  donatingId INT AUTO_INCREMENT UNIQUE,
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
);


CREATE TABLE OrganDonatingRegisterDate(
  dontatingRegisterId INT AUTO_INCREMENT PRIMARY KEY,
  dateReg Date,
  fkDonorId INT,
  FOREIGN KEY (fkDonorId) REFERENCES OrganDonating(donatingId) ON DELETE CASCADE
);

CREATE TABLE OrganDonatingDeRegisterDate(
  dontatingDeRegisterId INT AUTO_INCREMENT PRIMARY KEY,
  dateDeReg Date,
  fkDonorId INT,
  FOREIGN KEY (fkDonorId) REFERENCES OrganDonating(donatingId) ON DELETE CASCADE
);

CREATE TABLE OrganAwaitingRegisterDate(
  awaitingRegisterId INT AUTO_INCREMENT PRIMARY KEY,
  dateReg Date,
  fkRecieverId INT,
  FOREIGN KEY (fkRecieverId) REFERENCES OrganAwaiting(awaitingingId) ON DELETE CASCADE
);

CREATE TABLE OrganAwaitingDeRegisterDate(
  awaitingDeRegisterId INT AUTO_INCREMENT PRIMARY KEY,
  dateDeReg Date,
  fkRecieverId INT,
  FOREIGN KEY (fkRecieverId) REFERENCES OrganAwaiting(awaitingingId) ON DELETE CASCADE
);

CREATE TABLE HealthOrganDonate(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
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

CREATE TABLE MedicalProcedureOrgan(
  fkOrgansId SMALLINT,
  fkUserNhi VARCHAR(7),
  PRIMARY KEY (fkOrgansId, fkUserNhi),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkOrgansId) REFERENCES Organ(organId) ON DELETE CASCADE
);

CREATE TABLE MedicalProcedure(
  procedureName VARCHAR(255) NOT NULL,
  procedureDate DATE NOT NULL,
  fkUserNhi VARCHAR(7) NOT NULL,
  procedureDescription TEXT,
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
  emergencyContactId INT AUTO_INCREMENT PRIMARY KEY,
  contactName VARCHAR(255) NOT NULL ,
  contactRelationship VARCHAR(255) NOT NULL,
  homePhone VARCHAR(31),
  cellPhone VARCHAR(31),
  email VARCHAR(255),
  fkUserNhi VARCHAR(7) NOT NULL,
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE
);

CREATE TABLE Address(
  fkContactId INT,
  fkEmergencyContactId INT,
  streetNumber VARCHAR(15),
  streetName VARCHAR(255),
  neighbourhood VARCHAR(255),
  city VARCHAR(25),
  region VARCHAR(255),
  zipCode VARCHAR(15),
  country VARCHAR(255),
  fkUserNhi VARCHAR(7),
  fkStaffId INT,
  PRIMARY KEY (fkContactId, fkEmergencyContactId),
  FOREIGN KEY (fkUserNhi) REFERENCES User(nhi) ON DELETE CASCADE,
  FOREIGN KEY (fkStaffId) REFERENCES Clinician(staffId) ON DELETE CASCADE,
  FOREIGN KEY (fkContactId) REFERENCES ContactDetails(contactId) ON DELETE CASCADE,
  FOREIGN KEY (fkEmergencyContactId) REFERENCES EmergencyContactDetails(emergencyContactId) ON DELETE CASCADE
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