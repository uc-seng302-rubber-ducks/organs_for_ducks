package seng302.TestUtils;

import odms.commons.model.User;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

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
}
