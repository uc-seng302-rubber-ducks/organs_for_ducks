package seng302.Controller;

public class AppController {

  private static AppController controller;

  private  AppController() {
    //constructor goes here
  }

  /**
   * Returns the instance of the controller
   * @return AppController
   */
  public static AppController getInstance() {
    if (controller == null) {
      controller = new AppController();
    }
    return controller;
  }

  public boolean Register() {


    return true;
  }
}
