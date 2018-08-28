package odms.commons.database.db_strategies;

import odms.commons.model.Appointment;
import odms.commons.model._enum.db.appointment.statements.AppointmentStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class FetchClincianAppointmentsStrategy extends AbstractFetchAppointmentStrategy {
    /**
     * @see AbstractFetchAppointmentStrategy
     */
    @Override
    public Collection<Appointment> getAppointments(Connection connection, String userId, int count, int start) throws SQLException {
        Collection<Appointment> appointments = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(AppointmentStatement.GET_APPTS_FOR_CLINICIAN.getStatement())) {
            preparedStatement.setString(1, userId);
            preparedStatement.setInt(2, count);
            preparedStatement.setInt(3, start);
            try (ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    appointments.add(decodeAppointmentFromResultSet(results));
                }
            }
        }
        return appointments;
    }
}
