package seng302.Service;

import java.time.LocalDate;
import java.time.Period;
import javafx.scene.control.ComboBox;
import seng302.Model.BloodTypes;

import seng302.Model.User;
import seng302.Model.TransplantDetails;


/**
 * Contains methods for validating user/clinician attributes when updating/creating in the GUI application.
 * @author acb116, are66, eli26, jha236
 */
public class AttributeValidation {


    /**
     * Checks that the NHI matches the correct format.
     * @param nhi The national health index.
     * @return The NHI as a string if it matches the correct format, null otherwise.
     */
    public static String validateNHI(String nhi) {
        if (nhi.matches("[A-Za-z]{3}[0-9]{4}")) {
            return nhi;
        }
        return null;
    }


    /**
     * A basic regular expression for validating that emails are in the correct format.
     * Mainly checks that the '.' and '@' signs are in the correct place, the correct characters are used, and the
     * domain name is longer than one character.
     *
     * @param email The user input of an email address to be validated.
     * @return The given email if it is in the correct format, null otherwise.
     */
    public static String validateEmail(String email) {
        if (email.matches("^[a-zA-Z0-9][a-zA-Z0-9._%+-]*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")) {
            return email;
        } else return null;
    }


    /**
     * A basic regular expression for validating NZ landline telephone numbers.
     * The number must be 9 digits long, including a leading zero, a one-digit area code and a seven-digit phone number.
     *
     * @param phoneNum The user input of a NZ landline number to be validated.
     * @return The given phone number if it is in the correct format, null otherwise.
     */
    public static String validatePhoneNumber(String phoneNum) {
        if (phoneNum.matches("^[0][3|4|6|7|9]( |-)?[2-9][0-9]{2}( |-)?[0-9]{4}$")) {
            return phoneNum;
        } else return null;
    }


    /**
     * A basic regular expression for validating NZ cell phone numbers.
     * Checks that the number has a minimum length of 9 digits or a max of 11 digits (including the leading '02').
     *
     * @param cellNum The user input of a cell phone number to be validated.
     * @return The given cell phone number if it is in the correct format, null otherwise.
     */
    public static String validateCellNumber(String cellNum) {
        if (cellNum.matches("^[0-9]{7,13}$")) {
            return cellNum;
        } else return null;
    }

    /**
     * Checks if the given attribute is empty.
     * @param attribute The attribute to be checked.
     * @return The attribute as a string if it is not empty, null otherwise.
     */
    public static String checkString(String attribute) {
        if (attribute.isEmpty()) {
            return null;
        }
        return attribute;
    }

    /**
     * Checks that the date of birth is before the date of death and the date of death is before tomorrow's date if the
     * date of death is not null.
     * @param birth The date of birth.
     * @param death The date of death.
     * @return true if the date of death is null or the date of birth is before the date of death and the date of death
     * is before the current date, false otherwise.
     */
    public static boolean validateDates(LocalDate birth, LocalDate death) {
        return death == null || (birth.isBefore(death) && death.isBefore(LocalDate.now().plusDays(1)));
    }


    /**
     * Calculates the age of the user by comparing the date of birth with the date of death.
     * @param birth The date of birth.
     * @param death The date of death.
     * @return The age of the user.
     */
    public static int calculateAge(LocalDate birth, LocalDate death) {
        int age;

        if (death != null) {
            age = Period.between(birth, death).getYears();
        } else {
            age = Period.between(birth, LocalDate.now()).getYears();
        }

        return age;
    }



    /**
     * Gets the enum value of BloodTypes by iterating through the string literals
     * and matching them to the given blood type.
     * @param bloodBox the combobox containing blood types.
     * @return The enum of the given blood type if found, null otherwise.
     */
    public static String validateBlood(ComboBox bloodBox) {

        if (bloodBox.getValue() != null) {
            String blood = bloodBox.getValue().toString();
            for (BloodTypes type : BloodTypes.values()) {
                if ((type.toString()).equals(blood)) {
                    return blood;
                }
            }
        }

        return null;
    }


    /**
     * Checks that the given value can be parsed as a double.
     * @param stringValue Either the height or weight to be parsed as a double.
     * @return The value as a double, -1 if the value was less than or equal to 0 or there was an exception, otherwise 0 if the value was empty.
     */
    public static double validateDouble(String stringValue) {
        double doubleValue;

        if (!stringValue.isEmpty()) {
            try {
                doubleValue = Double.parseDouble(stringValue);
                if (doubleValue < 0) {
                    doubleValue = -1;
                }
            } catch (NumberFormatException e) {
                doubleValue = -1;
            }
        } else {
            doubleValue = 0;
        }
        return doubleValue;
    }


    /**
     * Gets the first character of the given gender.
     * @param genderBox The combobox containing gender types.
     * @return The gender value.
     */
    public static String validateGender(ComboBox genderBox) {
        String gender = "";

        if (genderBox.getValue() != null && !genderBox.getValue().toString().equals("")) {
            gender = genderBox.getValue().toString();
        }

        return gender;
    }

    /**
     * Check the entry of the string provided to see if the user's region matches the text
     * @param regionString String object to check against the user's region
     * @param user a User object
     * @return true if the user's regions starts with the provided string
     */
    public static boolean checkRegionMatches(String regionString, User user) {
        if (user.getRegion() == null) {
            return regionString.equals("");
        } else {
            return user.getRegion().toLowerCase().startsWith(regionString.toLowerCase());
        }
    }

    /**
     * Check the entry of the string provided to see if the user's region matches the text
     *
     * @param regionString String object to check against the user's region
     * @param transplantDetails a User object
     * @return true if the user's regions starts with the provided string
     */
    public static boolean checkRegionMatches(String regionString,
        TransplantDetails transplantDetails) {
        if (transplantDetails.getRegion() == null) {
            return regionString.equals("");
        } else {
            return transplantDetails.getRegion().toLowerCase()
                .startsWith(regionString.toLowerCase());
        }
    }

    /**
     * Check the entry of the string provided to see if the users gender matches the text
     * @param genderValue String object to check against the users gender
     * @param user a User object
     * @return true if the users gender starts with the provided string
     */
    public static boolean checkGenderMatches(String genderValue, User user) {
        if (user.getBirthGender() == null) {
            return genderValue.equals("All");
        }
        return (user.getBirthGender().equalsIgnoreCase(genderValue) ||
            genderValue.equalsIgnoreCase("All"));
    }

    public static boolean checkTextMatches(String text1, String text2) {
        if (text1 == text2) {
            return true;
        }
        if (text1 == null) {
            return text2.equals("");
        }
        if (text2 == null) {
            return text1.equals("");
        }
        return text1.toLowerCase().startsWith(text2.toLowerCase());
    }
}