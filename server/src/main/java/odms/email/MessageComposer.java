package odms.email;

import com.google.common.io.CharStreams;
import odms.commons.model.dto.AppointmentWithPeople;
import odms.commons.utils.Log;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * class to handle string formatting for sending an email about a given appointment.
 * All methods will either return a fully formatted message or an empty string.
 */
@Component
public class MessageComposer {

    public String writeAccepted(AppointmentWithPeople appointment) {
        String raw = readFile("messages/accepted.txt");
        if (raw != null) {
            return String.format(raw,
                    appointment.getUser().getFullName(),
                    appointment.getAppointmentTime().toLocalDate().toString(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName(),
                    appointment.getAppointmentTime().toLocalTime().toString(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName());
        }
        return "";
    }

    public String writeRejected(AppointmentWithPeople appointment) {
        String raw = readFile("messages/rejected.txt");
        if (raw != null) {
            return String.format(raw,
                    appointment.getUser().getFullName(),
                    appointment.getAppointmentTime().toLocalDate().toString(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName());
        }
        return "";
    }

    public String writeCancelledByClinician(AppointmentWithPeople appointment) {
        String raw = readFile("messages/cancelledByClinician.txt");
        if (raw != null) {
            return String.format(raw,
                    appointment.getUser().getFullName(),
                    appointment.getAppointmentTime().toLocalDate().toString(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName());
        }
        return "";
    }

    public String writeUpdated(AppointmentWithPeople appointment) {
        String raw = readFile("messages/updated.txt");
        if (raw != null) {
            return String.format(raw,
                    appointment.getUser().getFullName(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName(),
                    appointment.getAppointmentDateTimeString(),
                    appointment.getClinician().getFirstName(),
                    appointment.getClinician().getLastName());
        }
        return "";
    }


    /**
     * attempts to read a file in the resources package. returns null if it is not found/error while reading
     *
     * @param filename name of the file to read
     * @return String equivalent to the contents of the given file
     */
    private String readFile(String filename) {
        try {
            return CharStreams.toString(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(filename)));
        } catch (IOException ex) {
            Log.warning("could not find file " + filename, ex);
            return null;
        }
    }
}
