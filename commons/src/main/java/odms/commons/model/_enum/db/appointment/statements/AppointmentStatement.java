package odms.commons.model._enum.db.appointment.statements;

public enum AppointmentStatement {
    GET_APPTS_FOR_USER("SELECT * FROM AppointmentDetails JOIN AppointmentCategory ON fkCategoryId = categoryId WHERE fkUserNhi = ? LIMIT ? OFFSET ?"),
    GET_APPTS_FOR_CLINICIAN("SELECT * FROM AppointmentDetails JOIN AppointmentCategory ON fkCategoryId = categoryId WHERE fkStaffId = ? LIMIT ? OFFSET ?");

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
