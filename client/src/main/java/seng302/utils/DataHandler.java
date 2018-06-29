package seng302.utils;

import seng302.model.Administrator;
import seng302.model.Clinician;
import seng302.model.User;

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
     */
    public abstract boolean saveUsers(Collection<User> users) throws IOException;

    /**
     * Abstract method that loads the Users from a specified location into memory.
     *
     * @param location Location of where to load the data from
     */
    public abstract List<User> loadUsers(String location) throws FileNotFoundException;

    /**
     * Abstract method that may or may not save the collection of users to the default location
     *
     * @param clinicians Collection of clinicians to attempt to save
     * @return true if the operation succeeded, false otherwise
     */
    public abstract boolean saveClinicians(Collection<Clinician> clinicians) throws IOException;

    /**
     * Abstract method that may or may not load the clinicians from a specified location in memory.
     *
     * @param location Location of where to load the clinicians
     * @return true if the operation succeeded, false otherwise
     */
    public abstract List<Clinician> loadClinicians(String location) throws FileNotFoundException;

    /**
     * Abstract method that may or may not save the collection of administrators to the default location
     *
     * @param administrators Collection of administrators to attempt to save
     * @return true if the operation succeeded, false otherwise
     */
    public abstract boolean saveAdmins(Collection<Administrator> administrators) throws IOException;

    /**
     * Abstract method that may or may not load the clinicians from a specified location in memory.
     *
     * @param location Location to load the administrators from
     * @return true if the operation succeeded, false otherwise
     */
    public abstract Collection<Administrator> loadAdmins(String location) throws FileNotFoundException;
}
