package test_utils;

import odms.commons.model.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
}
