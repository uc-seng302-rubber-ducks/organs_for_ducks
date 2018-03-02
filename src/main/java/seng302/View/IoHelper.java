package seng302.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IoHelper {
  public static Date readDate(String rawDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date;
    try {
      date = sdf.parse(rawDate);
    }
    catch (ParseException e) {
      System.err.println("Error parsing date: "+rawDate);
      System.err.println("Please use format yyyy-MM-dd");
      date = null;
    }
    return date;
  }
}
