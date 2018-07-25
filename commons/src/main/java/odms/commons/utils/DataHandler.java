package odms.commons.utils;

import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class DataHandler {
    /**
     * Abstract method that may or may not save the collection of users to the default location
     *
     * @param users Collection of users to attempt to save
     * @return true if the operation succeeded, false otherwise
     * @throws IOException if the operation to save the users fails
     */
    public abstract boolean saveUsers(Collection<User> users) throws IOException;

    /**
     * Abstract method that loads the Users from a specified location into memory.
     *
     * @param location Location of where to load the data from
     * @return a list of users loaded from the file
     * @throws FileNotFoundException if the file is not found
     */
    public abstract List<User> loadUsers(String location) throws FileNotFoundException;

    /**
     * Abstract method that may or may not save the collection of users to the default location
     *
     * @param clinicians Collection of clinicians to attempt to save
     * @return true if the operation succeeded, false otherwise
     * @throws IOException if the operation fails
     */
    public abstract boolean saveClinicians(Collection<Clinician> clinicians) throws IOException;

    /**
     * Abstract method that may or may not load the clinicians from a specified location in memory.
     *
     * @param location Location of where to load the clinicians
     * @return true if the operation succeeded, false otherwise
     * @throws FileNotFoundException if the file provided is not found
     */
    public abstract List<Clinician> loadClinicians(String location) throws FileNotFoundException;

    /**
     * Abstract method that may or may not save the collection of administrators to the default location
     *
     * @param administrators Collection of administrators to attempt to save
     * @return true if the operation succeeded, false otherwise
     * @throws IOException if the save operation fails
     */
    public abstract boolean saveAdmins(Collection<Administrator> administrators) throws IOException;

    /**
     * Abstract method that may or may not load the clinicians from a specified location in memory.
     *
     * @param location Location to load the administrators from
     * @return true if the operation succeeded, false otherwise
     * @throws FileNotFoundException if the flie provided is not found
     */
    public abstract Collection<Administrator> loadAdmins(String location) throws FileNotFoundException;
}
