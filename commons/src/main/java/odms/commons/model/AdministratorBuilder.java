package odms.commons.model;

import odms.commons.model._abstract.IgnoreForUndo;

import java.time.LocalDateTime;

@IgnoreForUndo
public class AdministratorBuilder {
    private Administrator admin;

    public AdministratorBuilder() {
        admin = new Administrator();
    }

    public AdministratorBuilder setUserName(String userName) {
        admin.setUserName(userName);
        return this;
    }

    public AdministratorBuilder setFirstName(String firstName) {
        admin.setFirstName(firstName);
        return this;
    }

    public AdministratorBuilder setMiddleName(String middleName) {
        admin.setMiddleName(middleName);
        return this;
    }

    public AdministratorBuilder setLastName(String lastName) {
        admin.setLastName(lastName);
        return this;
    }

    public AdministratorBuilder setPassword(String password) {
        admin.setPassword(password);
        return this;
    }

    public AdministratorBuilder setSalt(String salt) {
        admin.setSalt(salt);
        return this;
    }

    public AdministratorBuilder setDateCreated(LocalDateTime dateCreated) {
        admin.setDateCreated(dateCreated);
        return this;
    }

    public Administrator build() {
        return admin;
    }
}
