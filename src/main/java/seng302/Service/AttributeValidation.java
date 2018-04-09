package seng302.Service;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import seng302.Model.BloodTypes;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;


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
     * Checks that the date of birth is before the date of death.
     * @param birth The date of birth.
     * @param death The dat of death.
     * @return true if the date of birth is valid and before the date of death, false otherwise.
     */
    public static boolean validateDates(LocalDate birth, LocalDate death) {
        return birth != null && (death == null || birth.isBefore(death));
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




//    /**
//     *
//     * @param values
//     * @param command
//     * @param type
//     * @return
//     */
//    public static String addMultipleValues(String values, String command, TextField type) {
//        if (!type.getText().isEmpty()) {
//            values += command + "\"" + type.getText() + "\"";
//        }
//
//        return values;
//    }
//
//
//    /**
//     *
//     * @param values
//     * @param command
//     * @param type
//     * @return
//     */
//    public static String addValues(String values, String command, TextField type) {
//        if (!type.getText().isEmpty()) {
//            values += command + type.getText();
//        }
//
//        return values;
//    }

//    public static String addPhoneNumber(String values, String command, TextField type) {
//        if (!type.getText().isEmpty()) {
//            values += command + type.getText().replaceAll("\\s+","");
//        }
//
//        return values;
//    }
//
//
//    public static String addComboSelection(ComboBox type) {
//        String value;
//        if (type.getValue() != null) {
//            value += command + type.getValue().toString();
//        }
//
//        return values;
//    }


    /**
     * Gets the enum value of BloodTypes by iterating through the string literals
     * and matching them to the given blood type.
     * @param blood The given blood type.
     * @return The enum of the given blood type if found, null otherwise.
     */
    public static BloodTypes validateBlood(String blood) {

        for (BloodTypes type : BloodTypes.values()) {
            if (type.equals(blood)) {
                return type;
            }
        }

        return null;
    }


    /**
     * Checks that the given height can be parsed as a Double.
     * @param height The height string to be parsed.
     * @return The height as a Double, or null if there was an exception.
     */
    public static Double validateHeight(String height) {
        Double dHeight;
        try {
            dHeight = Double.parseDouble(height);
        } catch (NumberFormatException e) {
            dHeight = null;
        }
        return dHeight;
    }


    /**
     * Checks that the given weight can be parsed as a Double.
     * @param weight The weight string to be parsed.
     * @return The weight as a Double, or null if there was an exception.
     */
    public static Double validateWeight(String weight) {
        Double dWeight;
        try {
            dWeight = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            dWeight = null;
        }
        return dWeight;
    }


    /**
     * Gets the first character of the given gender.
     * @param selectedGender The given gender.
     * @return The gender as a Character.
     */
    public static String validateGender(String selectedGender) {
        String gender = null;

        if (selectedGender != null) {
            gender = Character.toString(selectedGender.charAt(0));
        }

        return gender;
    }
}