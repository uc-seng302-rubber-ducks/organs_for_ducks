package odms.controller.gui.widget;

import javafx.scene.control.CheckBox;


/**
 * Adds the ability to get the label of a check box to display it in a list view
 */
public class TextStringCheckBox extends CheckBox {

    public TextStringCheckBox(String text) {
        super(text);
    }

    @Override
    public String toString() {
        return super.getText();
    }
}
