package odms.TestUtils;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.testfx.framework.junit.ApplicationTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FxRobotHelper {
    public static void clickButton(ApplicationTest test, String query) {
        test.lookup(query).queryAs(Button.class).getOnAction().handle(new ActionEvent());
    }

    public static void setTextField(ApplicationTest test, String query, String value) {
        test.lookup(query).queryAs(TextField.class).setText(value);
    }

    public static void setComboBoxValue(ApplicationTest test, String query, Object value) {
        test.lookup(query).queryAs(ComboBox.class).setValue(value);
    }

    public static void setDatePickerValue(ApplicationTest test, String query, Object value){
        test.lookup(query).queryAs(DatePicker.class).setValue(LocalDate.parse(value.toString(), DateTimeFormatter.ofPattern("d/M/yyyy")));
    }
}
