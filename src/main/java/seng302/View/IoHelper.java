package seng302.View;

import seng302.Model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Class to handle input and output
 */
public class IoHelper {

  /**
   * tries to convert a string to a date
   * requires format yyyy-MM-dd
   * writes to System.err on failure
   * @param rawDate The date as a String.
   * @return Date or null
   */
  public static LocalDate readDate(String rawDate) {
    DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date;
    try {
      date = LocalDate.parse(rawDate,sdf);
    } catch (DateTimeParseException e) {
      System.err.println("Error parsing date: " + rawDate);
      System.err.println("Please use format yyyy-MM-dd");
      date = null;
    }
    return date;
  }

  /**
   * updates the name of a user where either first or last name could be null.
   * Only replaces the non-null value
   * @param user The current user.
   * @param firstName The users first name.
   * @param lastName The users last name.
   * @return boolean if changes were made or not
   */
  public static boolean updateName(User user, String firstName, String lastName) {

    if (firstName == null && lastName == null) {
      return false;
    }

    //TODO review logic for edge cases
    String[] names = user.getFullName().split(" ");
    if (firstName != null && lastName != null) {
      user.setName(firstName , "" , lastName);
    }
    else if (lastName == null && names.length > 1) {
      user.setName(firstName , "" , names[1]);
    }
    else if (firstName == null) {
      user.setName(names[0] ,"", lastName);
    }
    return true;
  }

  /**
   *
   * @param users An array list of users.
   * @return A String of all users separated by a new line.
   */
  public static String prettyStringDonors(List<User> users) {
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
