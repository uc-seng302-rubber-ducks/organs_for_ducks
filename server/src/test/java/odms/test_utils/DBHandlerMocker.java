package odms.test_utils;

import odms.commons.model.Clinician;
import odms.commons.model.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

public class DBHandlerMocker {
    public static void setUserResultSet(ResultSet resultSet, User user) throws SQLException {
        when(resultSet.getString("nhi")).thenReturn(user.getNhi());
        when(resultSet.getString("firstName")).thenReturn(user.getFirstName());
        when(resultSet.getString("middleName")).thenReturn(user.getMiddleName());
        when(resultSet.getString("lastName")).thenReturn(user.getLastName());
        when(resultSet.getString("preferedName")).thenReturn(user.getPreferredFirstName());
        when(resultSet.getTimestamp("timeCreated")).thenReturn(Timestamp.valueOf(user.getTimeCreated()));
        when(resultSet.getTimestamp("lastModified")).thenReturn(Timestamp.valueOf(user.getLastModified()));
        when(resultSet.getString("gender")).thenReturn(user.getGenderIdentity());
        when(resultSet.getString("birthGender")).thenReturn(user.getBirthGender());
        when(resultSet.getDate("dob")).thenReturn(Date.valueOf(user.getDateOfBirth()));
        when(resultSet.getDate("dod")).thenReturn(user.getDateOfDeath() == null ? null : Date.valueOf(user.getDateOfDeath()));
        when(resultSet.getString("alcoholConsumption")).thenReturn(user.getAlcoholConsumption());
    }

    public static void setClinicianResultSet(ResultSet resultSet, Clinician clinician) throws SQLException {
        when(resultSet.getString("staffId")).thenReturn(clinician.getStaffId());
        when(resultSet.getString("firstName")).thenReturn(clinician.getFirstName());
        when(resultSet.getString("middleName")).thenReturn(clinician.getMiddleName());
        when(resultSet.getString("lastName")).thenReturn(clinician.getLastName());
        when(resultSet.getTimestamp("timeCreated")).thenReturn(Timestamp.valueOf(clinician.getDateCreated()));
        when(resultSet.getTimestamp("lastModified")).thenReturn(Timestamp.valueOf(clinician.getDateLastModified()));
    }

    public static void setTransplantResultSet(ResultSet resultSet) throws SQLException {
        when(resultSet.getString(1)).thenReturn("ABC1234");
        when(resultSet.getString(2)).thenReturn("Frank");
        when(resultSet.getString(3)).thenReturn("Jeffery");
        when(resultSet.getString(4)).thenReturn("Johnson");
        when(resultSet.getString(5)).thenReturn("LIVER");
        when(resultSet.getDate(6)).thenReturn(new Date(0));
        when(resultSet.getString(7)).thenReturn("over there");
    }

    public static void setDeathDetailsResultSet(ResultSet resultSet) throws SQLException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date dateOfDeath = sdf.parse("2010-01-01");
            java.sql.Date sqlDate = new java.sql.Date(dateOfDeath.getTime());

            SimpleDateFormat stf = new SimpleDateFormat("hh:mm");
            long ms = stf.parse("02:45").getTime();
            Time sqlTime = new Time(ms);

            when(resultSet.getString("fkUserNhi")).thenReturn("ABC1234");
            when(resultSet.getDate("dateOfDeath")).thenReturn(sqlDate);
            when(resultSet.getTime("timeOfDeath")).thenReturn(sqlTime);
            when(resultSet.getString("city")).thenReturn("Christchurch");
            when(resultSet.getString("region")).thenReturn("Canterbury");
            when(resultSet.getString("country")).thenReturn("New Zealand");

        } catch (ParseException p) {
            //Go away
        }
    }

    /**
     * Sets the result set to return the details of a basic appointment.
     *
     * @param resultSet mocked resultset to return the basic appointment details
     * @throws SQLException This shouldn't be thrown due to it being a mocked object.
     */
    public static void setAppointmentDetails(ResultSet resultSet) throws SQLException {
        when(resultSet.getInt("apptId")).thenReturn(0);
        when(resultSet.getTimestamp("requestedTime")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2018, 12, 10, 15, 3)));
        when(resultSet.getString("fkUserNhi")).thenReturn("ABC1234");
        when(resultSet.getString("fkStaffId")).thenReturn("0");
        when(resultSet.getString("description")).thenReturn("A description");
        when(resultSet.getInt("fkCategoryId")).thenReturn(1);
        when(resultSet.getInt("fkStatusId")).thenReturn(1);
    }

    /**
     * Causes the result set to return the details of a blood test
     * Units are specified on the side
     *
     * @param resultSet mocked result set to return basic blood test details
     * @throws SQLException
     */
    public static void setBloodTestResultSet(ResultSet resultSet) throws SQLException {
        when(resultSet.getInt("bloodTestId")).thenReturn(1);
        when(resultSet.getDouble("redBloodCellCount")).thenReturn(0.0054);  // x10^9 /L
        when(resultSet.getDouble("whiteBloodCellCount")).thenReturn(8.5);   // x10^9 /L
        when(resultSet.getDouble("haemoglobinLevel")).thenReturn(120.5);    // g/L
        when(resultSet.getDouble("platelets")).thenReturn(300.5);           // x10^9 /L
        when(resultSet.getDouble("glucoseLevels")).thenReturn(4.8);         // mmol/L
        when(resultSet.getDouble("meanCellVolume")).thenReturn(80.5);       // fL
        when(resultSet.getDouble("haematocrit")).thenReturn(0.35);          // ratio
        when(resultSet.getDouble("meanCellHaematocrit")).thenReturn(25.5);  // pg
        when(resultSet.getDate("requestedDate")).thenReturn(Date.valueOf(LocalDate.of(2018, 9, 15)));
    }

}
