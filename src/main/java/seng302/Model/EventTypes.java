package seng302.Model;

public enum EventTypes {
  USER_UPDATE("USER"),
  CLINICIAN_UPDATE("CLINICIAN");

  private final String name;

  EventTypes(String name) {
    this.name = name;
  }
}


