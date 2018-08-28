package odms.commons.database.db_strategies;

import odms.commons.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

public class AppointmentUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_APPOINTMENT_STMT = "INSERT INTO AppointmentDetails (fkUserNhi, fkStaffId, fkCategoryId, requestedTime, fkStatusId, description) VALUES (?,?,?,?,?,?)";

    /**
     * Updates a collection of recurring appointments
     *
     * @param roles      Collection of appointments to update
     * @param connection Connection to the target database
     * @param <T>        Appointment objects
     * @throws SQLException
     */
    @Override
    public <T> void update(Collection<T> roles, Connection connection) throws SQLException {
        // todo: use this for saving recurring appointments
    }

    /**
     * Creates a new appointment entry in the database
     *
     * @param connection  Connection to the target database
     * @param appointment Appointment to create a database entry for
     * @throws SQLException If there is an error storing the appointment into the database or the connection is invalid
     */
    public void postSingleAppointment(Connection connection, Appointment appointment) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(CREATE_APPOINTMENT_STMT)) {

            preparedStatement.setString(1, appointment.getRequestingUser().getNhi());
            preparedStatement.setString(2, appointment.getRequestedClinicianId());
            preparedStatement.setInt(3, appointment.getAppointmentCategory().getDbValue());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(appointment.getRequestedDate()));
            preparedStatement.setInt(5, appointment.getAppointmentStatus().getDbValue());
            preparedStatement.setString(6, appointment.getRequestDescription());
            preparedStatement.executeUpdate();
        }
    }
}
