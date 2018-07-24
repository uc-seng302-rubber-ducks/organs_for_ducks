package odms.TestUtils;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.testfx.framework.junit.ApplicationTest;

import java.time.LocalDate;

public class FxRobotHelper {
    public static void clickOnButton(ApplicationTest app, String query) {
        app.interact(() -> {
            app.lookup(query).queryAs(Button.class).getOnAction().handle(new ActionEvent());
        });
    }

    public static void setTextField(ApplicationTest app, String query, String text) {
        app.interact(() -> {
            app.lookup(query).queryAs(TextField.class).setText(text);
        });
    }

    public static void setDate(ApplicationTest app, String query, LocalDate date) {
        app.interact(() -> {
            app.lookup(query).queryAs(DatePicker.class).setValue(date);
        });
    }

    public static void setComboBox(ApplicationTest app, String query, Object value) {
        app.interact(() -> {
            app.lookup(query).queryAs(ComboBox.class).selectionModelProperty().setValue(value);
        });
    }
}
