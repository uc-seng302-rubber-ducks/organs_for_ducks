package seng302.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import seng302.Model.Donor;

public class IoHelper {

  /**
   * tries to convert a string to a date
   * requires format yyyy-MM-dd
   * writes to System.err on failure
   *
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
   * updates the name of a donor where either first or last name could be null.
   * Only replaces the non-null value
   * @return boolean if changes were made or not
   */
  public static boolean updateName(Donor donor, String firstName, String lastName) {

    if (firstName == null && lastName == null) {
      return false;
    }

    //TODO review logic for edge cases
    String[] names = donor.getName().split(" ");
    if (firstName != null && lastName != null) {
      donor.setName(firstName + " " + lastName);
    }
    else if (lastName == null && names.length > 1) {
      donor.setName(firstName + " " + names[1]);
    }
    else if (firstName == null) {
      donor.setName(names[0] +" "+ lastName);
    }
    return true;
  }

  public static String prettyStringDonors(ArrayList<Donor> donors) {
    StringBuilder sb = new StringBuilder();
    if(donors.size() > 0) {
      for (Donor d : donors) {
        sb.append(d.toString());
        sb.append("\n");
      }
    } else {
      sb.append("No donors found");
    }

    return sb.toString();
  }
}
