package odms.commons.database.db_strategies;

import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.UserType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

public class AppointmentUpdateStrategy extends AbstractUpdateStrategy {

    private static final String CREATE_APPOINTMENT_STMT = "INSERT INTO AppointmentDetails (fkUserNhi, fkStaffId, fkCategoryId, requestedTime, fkStatusId, description) VALUES (?,?,?,?,?,?)";
    private static final String PATCH_APPOINTMENT_STATUS_STMT = "UPDATE AppointmentDetails SET fkStatusId = ? WHERE apptId = ?";
    private static final String DELETE_APPOINTMENT_REJECTED_SEEN = "DELETE FROM AppointmentDetails WHERE apptId = ? AND fkStatusId = 7";
    private static final String DELETE_USER_CANCELLED_APPOINTMENTS = "DELETE FROM AppointmentDetails WHERE fkStatusId = ? AND fkUserNhi = ?";
    private static final String DELETE_CLINICIAN_CANCELLED_APPOINTMENTS = "DELETE FROM AppointmentDetails WHERE fkStatusId = ? AND fkStaffId = ?";

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
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Updates the status of an appointment in the database
     * @param connection Connection to the database
     * @param statusId id of the status to update to
     * @param appointmentId id of the appointment to update
     * @throws SQLException If there is an error updating the appointment in the database or the connection is invalid
     */
    public void patchAppointmentStatus(Connection connection, int statusId, int appointmentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(PATCH_APPOINTMENT_STATUS_STMT)) {

            preparedStatement.setInt(1, statusId);
            preparedStatement.setInt(2, appointmentId);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Runs sql to delete an appointment in the database with status of rejected seen (id 7)
     * @param connection Connection to the database
     * @param appointmentId id fo the appointment to update
     * @throws SQLException If there is an error updating the appointment in the database or the connection is invalid
     */
    public void deleteRejectedSeenStatus(Connection connection, int appointmentId) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_APPOINTMENT_REJECTED_SEEN)) {

            preparedStatement.setInt(1, appointmentId);
            preparedStatement.executeUpdate();
        }
    }


    /**
     * Removes all cancelled appointments that have been seen by the user / clinician from the database.
     *
     * @param connection Connection to the target database
     * @param id         Unique identifier of the user / clinician
     * @param role       Specifies if the given user type is a user or a clinician
     * @throws SQLException when the connection is invalid or an error occurred when deleting the appointments
     */
    public void deleteCancelledAppointments(Connection connection, String id, UserType role) throws SQLException {
        String deleteQuery = null;
        int status = -1;

        if (role == UserType.USER) {
            deleteQuery = DELETE_USER_CANCELLED_APPOINTMENTS;
            status = AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue();
        } else if (role == UserType.CLINICIAN) {
            deleteQuery = DELETE_CLINICIAN_CANCELLED_APPOINTMENTS;
            status = AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue();
        }

        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, status);
            stmt.setString(2, id);
            stmt.executeUpdate();
        }
    }
}
