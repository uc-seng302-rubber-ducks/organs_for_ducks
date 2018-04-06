package seng302.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import seng302.Model.Donor;
import seng302.Model.User;

public class IoHelper {

  /**
   * tries to convert a string to a date
   * requires format yyyy-MM-dd
   * writes to System.err on failure
   *
   * @return Date or null
   */
  public static Date readDate(String rawDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date;
    try {
      date = sdf.parse(rawDate);
    } catch (ParseException e) {
      System.err.println("Error parsing date: " + rawDate);
      System.err.println("Please use format yyyy-MM-dd");
      date = null;
    }
    return date;
  }

  /**
   * updates the name of a user where either first or last name could be null.
   * Only replaces the non-null value
   * @return boolean if changes were made or not
   */
  public static boolean updateName(User user, String firstName, String lastName) {

    if (firstName == null && lastName == null) {
      return false;
    }

    //TODO review logic for edge cases
    String[] names = user.getName().split(" ");
    if (firstName != null && lastName != null) {
      user.setName(firstName + " " + lastName);
    }
    else if (lastName == null && names.length > 1) {
      user.setName(firstName + " " + names[1]);
    }
    else if (firstName == null) {
      user.setName(names[0] +" "+ lastName);
    }
    return true;
  }

  public static String prettyStringDonors(ArrayList<User> users) {
    StringBuilder sb = new StringBuilder();
    if(users.size() > 0) {
      for (User u : users) {
        sb.append(u.toString());
        sb.append("\n");
      }
    } else {
      sb.append("No users found");
    }

    return sb.toString();
  }
}
