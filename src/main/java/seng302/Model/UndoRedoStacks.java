package seng302.Model;

import org.joda.time.DateTime;

import java.util.Iterator;
import java.util.Stack;

public class UndoRedoStacks {

    static Stack<Donor> undoStack = new Stack<Donor>();
    static Stack<Donor> redoStack = new Stack<Donor>();


    //This probably shouldn't be in this class, so left Public
    /**
     * Clones a the current Donor instance onto the Donor instance 'donorClone'
     * @param donorClone The Donor instance to be cloned too
     * @return donorClone with the cloned attributes
     */
    public static Donor cloneDonor(Donor donor, Donor donorClone) {
        DateTime lastModified = donor.getLastModified();
        donorClone.setDateOfBirth(donor.getDateOfBirth());
        donorClone.setDateOfDeath(donor.getDateOfDeath());
        donorClone.setGender(donor.getGender());
        donorClone.setHeight(donor.getHeight());
        donorClone.setWeight(donor.getWeight());
        donorClone.setBloodType(donor.getBloodType());
        donorClone.setCurrentAddress(donor.getCurrentAddress());
        donorClone.setRegion(donor.getRegion());
        donorClone.setTimeCreated(donor.getTimeCreated());
        donorClone.setName(donor.getName());
        donorClone.setDeceased(donor.getDeceased());
        //donorClone.setMiscAttributes(donor.getMiscAttributes());

        donorClone.getMiscAttributes().clear();
        for(String misc : donor.getMiscAttributes()) {
            donorClone.addAttribute(misc);
        }
        //donorClone.initOrgans();
        donorClone.getOrgans().clear();
        if (donor.getOrgans() != null) {
            for (Organs organ : donor.getOrgans()) {
                donorClone.addOrgan(organ);
            }
        }

        //All .set functions call updateLastModified, not sure if we want undo to be an update
        donorClone.setLastModified(lastModified);

        return donorClone;
    }

    /**
     * Called when the 'Confirm' button is clicked. Stores the latest saves of the current donor
     * @param donor The current state of the donor to be cloned and pushed to the undo stack.
     */
    public static void storeUndoCopy(Donor donor) {
        if (donor.getName() != null) {
            Donor undoCopy = new Donor();
            undoCopy = cloneDonor(donor, undoCopy);

            undoStack.push(undoCopy);

        }
        while (!redoStack.empty()) { //When a manual save is created the redo stack must be emptied
            redoStack.pop(); //I guess they go to the void?
        }
    }

    /**
     * Called when the 'Undo' button is clicked. Loads the latest donor save and stores the current one in redoStack
     * @param donor Current donor save to go on the redo stack
     * @return donor Donor the latest donor save
     */
    public static Donor loadUndoCopy(Donor donor) {
        //Donor undoCopy = new Donor();
        if (!undoStack.empty()) { //Make sure that and Undo can be done
            Donor redoCopy = new Donor(); //Create a donor instance to be the redoCopy
            redoCopy = cloneDonor(donor, redoCopy); //Copy the data from the current save
            redoStack.push(redoCopy); //Push the redo copy

            Donor undoCopy = undoStack.pop();//Make the current donor the latest undo copy
            donor = cloneDonor(undoCopy, donor);

        }
        return donor; //Return the newly undone donor
    }

    /**
     * Called when the 'Redo' button is clicked. Undoes the last 'Undo' click, unless the undo/redo chain is broken by manual save
     * @param donor Current donor save to go on the undo stack
     * @return donor New donor save from the redo stack
     */
    public static Donor loadRedoCopy(Donor donor) {
        if (!redoStack.empty()) {
            Donor undoCopy = new Donor();
            undoCopy = cloneDonor(donor, undoCopy);
            undoStack.push(undoCopy);

            donor = redoStack.pop();
        }
        return donor;
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
