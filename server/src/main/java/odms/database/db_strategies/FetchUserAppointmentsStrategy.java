package odms.database.db_strategies;

import odms.commons.model.Appointment;
import odms.commons.model._enum.db.appointment.statements.AppointmentStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class FetchUserAppointmentsStrategy extends AbstractFetchAppointmentStrategy {
    /**
     * @see AbstractFetchAppointmentStrategy
     */
    @Override
    public Collection<Appointment> getAppointments(Connection connection, String userId, int count, int start) throws SQLException {
        Collection<Appointment> appointments = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(AppointmentStatement.GET_APPTS_FOR_USER.getStatement())) {
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

    /**
     * Gets an appointment that is accepted or rejected, but not seen by the user. This assumes that the user can not have
     * multiple unseen appointments, as they can only have one pending appointment and must mark others as seen. Even
     * if it becomes the case that the user has two Appointments unseen, one will show up the first time they log in, and
     * the other the next time, so it is not a major issue.
     *
     * @param connection Non null connection to the database
     * @param userId     Id of the user to search for an unseen appointment for
     * @return An unseen appointment that will be used to populate an alert window before being marked as seen
     * @throws SQLException if the connection is invalid or there is an error with the database
     */
    public Appointment getUnseenAppointment(Connection connection, String userId) throws SQLException {
        Appointment appointment = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(AppointmentStatement.GET_UNSEEN_APPTS_FOR_USER.getStatement())) {
            preparedStatement.setString(1, userId);
            try (ResultSet results = preparedStatement.executeQuery()) {
                while (results.next()) {
                    appointment = decodeAppointmentFromResultSet(results);
                }
            }
        }
        return appointment;
    }
}
