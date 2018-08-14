package odms.controller.gui.widget;

import javafx.scene.control.CheckBox;

public class TextStringCheckBox extends CheckBox {

    public TextStringCheckBox(String text) {
        super(text);
    }

    @Override
    public String toString() {
        return super.getText();
    }
}
