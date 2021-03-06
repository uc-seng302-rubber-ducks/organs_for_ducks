package odms.commons.utils;

import odms.commons.model._enum.AlcoholLevel;
import odms.commons.model._enum.BloodTypes;

import java.time.LocalDate;


/**
 * Contains methods for validating user/clinician attributes when updating/creating in the GUI application.
 *
 * @author acb116, are66, eli26, jha236
 */
public class AttributeValidation {

    private AttributeValidation() {
    }

    /**
     * Checks that the NHI matches the correct format.
     *
     * @param nhi The national health index.
     * @return True if NHI matches format, false otherwise
     */
    public static boolean validateNHI(String nhi) {
        return nhi.matches("[A-Za-z]{3}[0-9]{4}");
    }


    /**
     * A basic regular expression for validating that emails are in the correct format.
     * Mainly checks that the '.' and '@' signs are in the correct place, the correct characters are used, and the
     * domain name is longer than one character.
     *
     * @param email The user input of an email address to be validated.
     * @return True if the email is valid, false otherwise
     */
    public static boolean validateEmail(String email) {
        return email.matches("^[a-zA-Z0-9][a-zA-Z0-9._\\-]*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$") || email.isEmpty();
    }


    /**
     * A basic regular expression for validating NZ landline telephone numbers.
     * The number must be 9 digits long, including a leading zero, a one-digit area code and a seven-digit phone number.
     *
     * @param phoneNum The user input of a NZ landline number to be validated.
     * @return True if the phone number is valid, false otherwise
     */
    public static boolean validatePhoneNumber(String phoneNum) {
        return (phoneNum.matches("^\\+?[0-9]{1,3}([ \\-]?[0-9]{2,4}){2,3}$")) || phoneNum.isEmpty();
    }


    /**
     * A basic regular expression for validating NZ cell phone numbers.
     * Checks that the number has a minimum length of 9 digits or a max of 11 digits (including the leading '02').
     *
     * @param cellNum The user input of a cell phone number to be validated.
     * @return true if the cell number is valid, false otherwise
     */
    public static boolean validateCellNumber(String cellNum) {
        return (cellNum.matches("^\\+?[0-9]{1,3}[0-9]{7,13}$")) || cellNum.isEmpty();
    }

    /**
     * Checks if the given non-null attribute only contains a-z, A-Z, spaces, apostrophes and hyphens
     *
     * @param attribute The attribute to be checked.
     * @return true if the attribute meets the specified criteria, false otherwise
     */
    public static boolean checkString(String attribute) {
        return attribute != null && (attribute.matches("[a-zA-Z '\\-0-9]*"));
    }

    /**
     * Checks if the given non-null attribute is non-empty and only contains a-z, A-Z, spaces, apostrophes, hyphens and numbers
     *
     * @param attribute The attribute to be checked.
     * @return true if the attribute meets the specified criteria, false otherwise
     */
    public static boolean checkRequiredString(String attribute) {
        return attribute != null && (attribute.matches("[a-zA-Z '\\-0-9]+"));
    }

    /**
     * Checks if the given non-null attribute is non-empty and only contains a-z, A-Z, spaces, apostrophes and hyphens.
     *
     * @param attribute The attribute to be checked.
     * @return true if the attribute meets the specified criteria, false otherwise
     */
    public static boolean checkRequiredStringName(String attribute) {
        return attribute != null && (attribute.matches("[a-zA-Z '\\-]+"));
    }

    /**
     * Checks that the date of birth is before the date of death and the date of death is before tomorrow's date if the
     * date of death is not null.
     *
     * @param birth The non null date of birth.
     * @param death The date of death.
     * @return true if the date of death is null or the date of birth is before the date of death and the date of death
     * is before the current date, false otherwise.
     */
    public static boolean validateDateOfDeath(LocalDate birth, LocalDate death) {
        return death == null || (birth.isBefore(death.plusDays(1)) && death.isBefore(LocalDate.now().plusDays(1)));
    }

    /**
     * checks that date of birth is before tomorrow's date if the
     * date of birth is not null.
     *
     * @param date date
     * @return true if date of death is
     * before the current date, false otherwise.
     */
    public static boolean validateDateBeforeTomorrow(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now().plusDays(1));
    }

    /**
     * checks that appointment date is after
     * today's date if appointment date is not null.
     *
     * @param apptDate appointment date
     * @return true if appointment date is
     * after the current date, false otherwise.
     */
    public static boolean validateDateOfAppointment(LocalDate apptDate) {
        return apptDate != null && apptDate.isAfter(LocalDate.now());
    }

    /**
     * checks that Eligible Organ Date is after
     * today's date. Eligible date is optional field.
     *
     * @param eligibleDate date to allow organs to be eligible for
     *                     donation again
     * @return true if appointment date is
     * after the current date, false otherwise.
     */
    public static boolean validateEligibleOrganDate(LocalDate eligibleDate) {
        return eligibleDate == null || eligibleDate.isAfter(LocalDate.now());
    }

    /**
     * Gets the enum value of BloodTypes by iterating through the string literals
     * and matching them to the given blood type.
     *
     * @param blood the value of blood type.
     * @return True if the provided object is valid
     */
    public static boolean validateBlood(String blood) {
        if (!blood.equals("")) {
            for (BloodTypes type : BloodTypes.values()) {
                if ((type.toString()).equals(blood)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }

    /**
     * Gets the enum value of AlcoholLevel by iterating through the string literals
     * and matching them to the given Alcohol Level.
     *
     * @param alcoholLvl the alcohol level.
     * @return True if the provided object is valid
     */
    public static boolean validateAlcoholLevel(String alcoholLvl) {
        if (!alcoholLvl.equals("")) {
            for (AlcoholLevel lvl : AlcoholLevel.values()) {
                if ((lvl.toString()).equals(alcoholLvl)) {
                    return true;
                }
            }
            return false;
        }

        return true;
    }


    /**
     * Checks that the given value can be parsed as a double.
     *
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
     *
     * @param gender The string to be passed as a gender
     * @return The gender value.
     */
    public static boolean validateGender(String gender) {
        String[] valids = {"", "male", "female", "non binary"};
        for (String valid : valids) {
            if (gender.equalsIgnoreCase(valid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the string is in a 24 hour hh:mm format
     * @param time String to be checked
     * @return True if string is in correct format
     */
    public static boolean validateTimeString(String time) {
        return time.matches("(([01][0-9])|(2[0-3])):([0-5][0-9])");
    }

    /**
     * Checks if the given value is greater than or equal to the given lower bound
     *
     * @param lowerBound Lower bound of a blood test property
     * @param value Blood test property value
     * @return true if the given value is greater than or equal to the lower bound, false otherwise
     */
    public static boolean checkAboveLowerBound(double lowerBound, double value) {
        return value >= lowerBound;
    }

    /**
     * Checks if the given value is less than or equal to the upper bound
     *
     * @param upperBound Higher bound of a blood test property
     * @param value Blood test property value
     * @return true if the given value is less than or equal to the upper bound, false otherwise
     */
    public static boolean checkBelowUpperBound(double upperBound, double value) {
        return value <= upperBound;
    }
}