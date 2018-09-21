package odms.commons.database.db_strategies;

import odms.commons.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

public class AppointmentUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_APPOINTMENT_STMT = "INSERT INTO AppointmentDetails (fkUserNhi, fkStaffId, fkCategoryId, requestedTime, fkStatusId, description, title) VALUES (?,?,?,?,?,?,?)";
    private static final String UPDATE_APPOINTMENT_STMT = "UPDATE AppointmentDetails SET fkCategoryId = ?, requestedTime = ?, fkStatusId = ?, description = ?, title = ?" +
            "WHERE apptId = ?";
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

            preparedStatement.setString(1, appointment.getRequestingUserId());
            preparedStatement.setString(2, appointment.getRequestedClinicianId());
            preparedStatement.setInt(3, appointment.getAppointmentCategory().getDbValue());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(appointment.getRequestedDate()));
            preparedStatement.setInt(5, appointment.getAppointmentStatus().getDbValue());
            preparedStatement.setString(6, appointment.getRequestDescription());
            preparedStatement.setString(7, appointment.getTitle());
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Updates an existing appointment in the database
     * @param connection Connection to the target database
     * @param appointment Appointment to updated details from
     * @throws SQLException If there is an error updating the appointment in the database or the connection is invalid
     */
    public void putSingleAppointment(Connection connection, Appointment appointment) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_APPOINTMENT_STMT)) {
            preparedStatement.setInt(1, appointment.getAppointmentCategory().getDbValue());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(appointment.getRequestedDate()));
            preparedStatement.setInt(3, appointment.getAppointmentStatus().getDbValue());
            preparedStatement.setString(4, appointment.getRequestDescription());
            preparedStatement.setInt(5, appointment.getAppointmentId());
            preparedStatement.setString(6, appointment.getTitle());
            preparedStatement.executeUpdate();
        }
    }
}
