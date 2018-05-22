package seng302.steps;

import seng302.Controller.AppController;

public class CucumberTestModel {

  private static AppController controller = AppController.getInstance();
  private static String userNhi;

  public static AppController getController() {
    return controller;
  }

  public static void setUserNhi(String nhi) {
    userNhi = nhi;
  }

  public static String getUserNhi() {
    return userNhi;
  }
}
