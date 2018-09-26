package odms.view;

import odms.commons.model.User;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;

/**
 * Class to handle input and output
 */
public class IoHelper {

    private IoHelper() {
        //Hides the implicit constructor
    }

    /**
     * tries to convert a string to a date
     * requires format yyyy-MM-dd
     * writes to System.err on failure
     *
     * @param rawDate The date as a String.
     * @return Date or null
     */
    public static LocalDate readDate(String rawDate) {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date;
        try {
            date = LocalDate.parse(rawDate, sdf);
            Log.info(date.toString());
        } catch (DateTimeParseException e) {
            display("Error parsing date: " + rawDate);
            display("Please use format yyyy-MM-dd");
            date = null;
        }
        return date;
    }

    /**
     * updates the name of a user where either first or last name could be null.
     * Only replaces the non-null value
     *
     * @param user      The current user.
     * @param firstName The users first name.
     * @param lastName  The users last name.
     * @return boolean if changes were made or not
     */
    public static boolean updateName(User user, String firstName, String lastName) {

        if (firstName == null && lastName == null) {
            return false;
        }

        String[] names = user.getFullName().split(" ");
        if (firstName != null && lastName != null) {
            user.setName(firstName.replaceAll("_", " "), null, lastName.replaceAll("_", " "));
        } else if (lastName == null && names.length > 1) {
            user.setName(firstName.replaceAll("_", " "), null, names[1]);
        } else if (firstName == null) {
            user.setName(names[0], null, lastName.replaceAll("_", " "));
        }
        return true;
    }

    /**
     * @param users An array list of users.
     * @return A String of all users separated by a new line.
     */
    public static String prettyStringUsers(List<User> users) {
        StringBuilder sb = new StringBuilder();
        if (!users.isEmpty()) {
            for (User u : users) {
                sb.append(u.toString());
                sb.append("\n");
            }
        } else {
            sb.append("No users found");
        }

        return sb.toString();
    }

    public static String prettyStringUsers(Collection<UserOverview> users) {
        StringBuilder sb = new StringBuilder();
        if (!users.isEmpty()) {
            for (UserOverview u : users) {
                sb.append(u.toString());
                sb.append("\n");
            }
        } else {
            sb.append("No users found");
        }

        return sb.toString();
    }

    public static void display(String toShow) {
        System.out.println(toShow); //NOSONAR
        //This writes the strings to the GUI CLI
    }
}
