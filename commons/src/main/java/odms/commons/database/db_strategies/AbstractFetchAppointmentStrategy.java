package odms.commons.database.db_strategies;

import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public abstract class AbstractFetchAppointmentStrategy {
    /**
     * Gets all the appointments of with the userid, depending on the user type.
     *
     * @param connection Non null connection to the database
     * @param userId     identifier of the user
     * @param count      number of appointments to return
     * @param start      how many entries to skip before returning
     * @return A collection of appointments
     * @throws SQLException if the connection is invalid or there is an error with the database
     */
    public abstract Collection<Appointment> getAppointments(Connection connection, String userId, int count, int start) throws SQLException;

    /**
     * Takes a result set and returns an appointment based on the current row of the result set
     * @param results result set with the cursor set to the row to generate the appointment from
     * @return the appointment
     * @throws SQLException if there was an error with the result set
     */
    protected Appointment decodeAppointmentFromResultSet(ResultSet results) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(results.getInt("apptId"));
        appointment.setRequestedDate(results.getTimestamp("requestedTime").toLocalDateTime());
        appointment.setRequestedClinician(results.getString("fkStaffId"));
        appointment.setRequestingUser(results.getString("fkUserNhi"));
        appointment.setRequestDescription(results.getString("description"));
        for (AppointmentCategory category : AppointmentCategory.values()) {
            if (category.getDbValue() == results.getInt("fkCategoryId")) {
                appointment.setAppointmentCategory(category);
            }
        }
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.getDbValue() == results.getInt("fkStatusId")) {
                appointment.setAppointmentStatus(status);
            }
        }
        return appointment;
    }
}
