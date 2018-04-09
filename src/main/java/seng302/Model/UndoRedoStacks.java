package seng302.Model;

import java.time.LocalDateTime;
import java.util.Stack;

public class UndoRedoStacks {

    static Stack<User> undoStack = new Stack<>();
    static Stack<User> redoStack = new Stack<>();


    //This probably shouldn't be in this class, so left Public
    /**
     * Clones a the current Donor instance onto the Donor instance 'userClone'
     * @param userClone The Donor instance to be cloned too
     * @return userClone with the cloned attributes
     */
    public static User cloneUser(User user, User userClone) {
        LocalDateTime lastModified = user.getLastModified();
        userClone.setDateOfBirth(user.getDateOfBirth());
        userClone.setDateOfDeath(user.getDateOfDeath());
        userClone.setGender(user.getGender());
        userClone.setHeight(user.getHeight());
        userClone.setWeight(user.getWeight());
        userClone.setBloodType(user.getBloodType());
        userClone.setCurrentAddress(user.getCurrentAddress());
        userClone.setRegion(user.getRegion());
        userClone.setTimeCreated(user.getTimeCreated());
        userClone.setName(user.getName());
        userClone.setDeceased(user.getDeceased());
        //userClone.setMiscAttributes(user.getMiscAttributes());

        userClone.getMiscAttributes().clear();
        for(String misc : user.getMiscAttributes()) {
            userClone.addAttribute(misc);
        }

        userClone.getDonorDetails().initOrgans();
        userClone.getDonorDetails().getOrgans().clear();
        if (user.getDonorDetails().getOrgans() != null) {
            for (Organs organ : user.getDonorDetails().getOrgans()) {
                userClone.getDonorDetails().addOrgan(organ);
            }
        }

        //All .set functions call updateLastModified, not sure if we want undo to be an update
        userClone.setLastModified(lastModified);

        return userClone;
    }

    /**
     * Called when the 'Confirm' button is clicked. Stores the latest saves of the current user
     * @param user The current state of the user to be cloned and pushed to the undo stack.
     */
    public static void storeUndoCopy(User user) {
        if (user.getName() != null) { //Can't remember what this is for.
            User undoCopy = new User();
            undoCopy = cloneUser(user, undoCopy);

            undoStack.push(undoCopy);

        }
        while (!redoStack.empty()) { //When a manual save is created the redo stack must be emptied
            redoStack.pop(); //I guess they go to the void?
        }
    }

    /**
     * Called when the 'Undo' button is clicked. Loads the latest user save and stores the current one in redoStack
     * @param user Current user save to go on the redo stack
     * @return user Donor the latest user save
     */
    public static User loadUndoCopy(User user) {
        //Donor undoCopy = new Donor();
        if (!undoStack.empty()) { //Make sure that and Undo can be done
            User redoCopy = new User(); //Create a user instance to be the redoCopy
            redoCopy = cloneUser(user, redoCopy); //Copy the data from the current save
            redoStack.push(redoCopy); //Push the redo copy

            User undoCopy = undoStack.pop();//Make the current user the latest undo copy
            user = cloneUser(undoCopy, user);

        }
        return user; //Return the newly undone user
    }

    /**
     * Called when the 'Redo' button is clicked. Undoes the last 'Undo' click, unless the undo/redo chain is broken by manual save
     * @param user Current user save to go on the undo stack
     * @return user New user save from the redo stack
     */
    public static User loadRedoCopy(User user) {
        if (!redoStack.empty()) {
            User undoCopy = new User();
            undoCopy = cloneUser(user, undoCopy);
            undoStack.push(undoCopy);

            user = redoStack.pop();
        }
        return user;
    }


    /**
     * Clears both undo/redo stacks. Called when the 'Undo/Redo Session' is deemed to have ended
     */
    public static void clearStacks() {
        while (!undoStack.empty()) {
            undoStack.pop();
        }

        while (!redoStack.empty()) {
            redoStack.pop();
        }
    }



}
