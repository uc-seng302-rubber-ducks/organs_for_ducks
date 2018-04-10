package seng302.Service;

import javafx.scene.control.ComboBox;
import seng302.Model.BloodTypes;

import java.time.LocalDate;
import java.time.Period;


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
        if (nhi.matches("[A-Z]{3}[0-9]{4}")) {
            return nhi;
        }
        return null;
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
     *
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
     * Checks that the given height can be parsed as a Double.
     * @param height The height string to be parsed.
     * @return The height as a Double, or null if there was an exception.
     */
    public static double validateHeight(String height) {
        double dHeight;
        try {
            dHeight = Double.parseDouble(height);
        } catch (NumberFormatException e) {
            dHeight = 0;
        }
        return dHeight;
    }


    /**
     * Checks that the given weight can be parsed as a Double.
     * @param weight The weight string to be parsed.
     * @return The weight as a Double, or null if there was an exception.
     */
    public static double validateWeight(String weight) {
        double dWeight;
        try {
            dWeight = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            dWeight = 0;
        }
        return dWeight;
    }


    /**
     * Gets the first character of the given gender.
     *
     * @return The gender as a Character.
     */
    public static String validateGender(ComboBox type) {
        String gender = null;

        if (type.getValue() != null && type.getValue().toString() != null) {
            gender = Character.toString(type.getValue().toString().charAt(0));
        }

        return gender;
    }
}