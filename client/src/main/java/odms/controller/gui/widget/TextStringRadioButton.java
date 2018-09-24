package odms.controller.gui.widget;

import javafx.scene.control.RadioButton;

/**
 * Adds the ability to get the label of a radio button to display it in a list view
 */
public class TextStringRadioButton extends RadioButton {


    public TextStringRadioButton(String text) {
        super(text);
    }

    @Override
    public String toString() {
        return super.getText();
    }
}
