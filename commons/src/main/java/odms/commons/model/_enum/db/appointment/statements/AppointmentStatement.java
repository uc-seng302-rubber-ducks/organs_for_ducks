package odms.commons.model._enum.db.appointment.statements;

public enum AppointmentStatement {
    GET_APPTS_FOR_USER("SELECT * FROM AppointmentDetails JOIN AppointmentCategory ON fkCategoryId = categoryId WHERE fkUserNhi = ? LIMIT ? OFFSET ?"),
    GET_APPTS_FOR_CLINICIAN("SELECT * FROM AppointmentDetails JOIN AppointmentCategory ON fkCategoryId = categoryId WHERE fkStaffId = ? AND fkStatusId != 3 LIMIT ? OFFSET ?"),
    GET_UNSEEN_APPTS_FOR_USER("SELECT * FROM AppointmentDetails WHERE fkUserNhi = ? AND fkStatusId = 2 OR fkStatusId = 3");

    private String statement;

    AppointmentStatement(String statement) {
        this.statement = statement;
    }

    /**
     * Returns the statement associated with the enum
     */
    public String getStatement() {
        return statement;
    }
}
