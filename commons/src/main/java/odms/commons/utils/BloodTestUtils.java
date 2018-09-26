package odms.commons.utils;

import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import odms.commons.model._enum.BloodTestProperties;

import java.text.DecimalFormat;

public class BloodTestUtils {

    public static void invalidateNode(Node node) {
        node.getStyleClass().add("invalid");
    }

    /**
     * a method to check blood test properties and set error labels if they are invalid
     * @param textField the textfield containing the value for a blood test property
     * @param label the error label for a blood test property
     * @param bloodTestProperties the BloodTestProperty to get the upper and lower bound
     * @return returns true if the value in the textfield is a valid input
     */
    public static Boolean bloodTestValidation(TextField textField, Label label, BloodTestProperties bloodTestProperties){
        Boolean valid = true;
        DecimalFormat df2 = new DecimalFormat(".##");
        double value = AttributeValidation.validateDouble(textField.getText());
        if (value == -1){
            label.setVisible(true);
            invalidateNode(textField);
            valid = false;
        } else if (value > (bloodTestProperties.getUpperBound()) * 5.0){
            label.setText("that number is too large the max number is " + df2.format(bloodTestProperties.getUpperBound() * 5.0));
            label.setVisible(true);
            invalidateNode(textField);
            valid = false;
        } else if (value < (bloodTestProperties.getLowerBound() / 5.0) && value != 0.0) {
            label.setText("that number is too small the min number is " + df2.format(bloodTestProperties.getLowerBound() / 5.0));
            label.setVisible(true);
            invalidateNode(textField);
            valid = false;
        }
        return valid;
    }


    /**
     * removes the invalid field if the user starts typing
     *
     * @param field The current textfield.
     */
    public static void textFieldListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            removeInvalid(field);
        });

    }

    /**
     * Changes the title bar to add/remove an asterisk when a change was detected on the date picker.
     *
     * @param dp The current date picker.
     */
    public static void datePickerListener(DatePicker dp) {
        dp.valueProperty().addListener((observable, oldValue, newValue) -> {
            dp.getStyleClass().remove("invalid");

        });
    }

    public static void removeInvalid (TextField textField) {
        textField.getStyleClass().remove("invalid");
    }

}
