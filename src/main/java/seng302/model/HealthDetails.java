package seng302.model;

import seng302.model._enum.BloodTypes;

/**
 * Class for health details for a user
 */
public class HealthDetails {

    private String birthGender;
    private String genderIdentity;
    private String alcoholConsumption;
    private boolean smoker;
    private double height;
    private transient String heightText;
    private double weight;
    private transient String weightText;
    private String bloodType;
    private transient User attachedUser;

    public HealthDetails(User attachedUser) {
        this.birthGender = "";
        this.genderIdentity = "";
        this.alcoholConsumption = "None";
        this.smoker = false;
        this.height = 0.0;
        this.weight = 0.0;
        this.heightText = "";
        this.weightText = "";
        this.bloodType = null;
        this.attachedUser = attachedUser;
    }


    /**
     * Creates a copy of the current health details
     * @param healthDetails the original health details
     * @return a copy of the current health details
     */
    private HealthDetails copyHealthDetails(HealthDetails healthDetails) {
        if (healthDetails == null) {
            return null;
        }

        HealthDetails newHealthDetails = new HealthDetails(healthDetails.attachedUser);
        newHealthDetails.birthGender = healthDetails.birthGender;
        newHealthDetails.genderIdentity = healthDetails.genderIdentity;
        newHealthDetails.alcoholConsumption = healthDetails.alcoholConsumption;
        newHealthDetails.smoker = healthDetails.smoker;
        newHealthDetails.height = healthDetails.height;
        newHealthDetails.weight = healthDetails.weight;
        newHealthDetails.heightText = healthDetails.heightText;
        newHealthDetails.weightText = healthDetails.weightText;
        newHealthDetails.bloodType = healthDetails.bloodType;

        return newHealthDetails;
    }


    public double getHeight() {
        return height;
    }

    /**
     * Updates the height value and pushes the change onto the undo stack
     * @param height the new height
     */
    public void setHeight(double height) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        clone.setHealthDetails(copyHealthDetails(this));
        mem.setOldObject(clone);

        attachedUser.updateLastModified();
        if (this.height != height) {
            this.height = height;
            clone = attachedUser.clone();
            clone.setHealthDetails(copyHealthDetails(this));
            attachedUser.addChange(new Change("Changed height to " + height));
            mem.setNewObject(clone);
            attachedUser.getUndoStack().push(mem);

        }
    }

    public double getWeight() {
        return weight;
    }


    public void setWeight(double weight) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);

        attachedUser.updateLastModified();
        if (weight != this.weight) {
            this.weight = weight;
            attachedUser.addChange(new Change("Changed weight to " + weight));
            mem.setNewObject(clone);
            attachedUser.getUndoStack().push(mem);
        }
    }

    public String getHeightText() {
        return heightText;
    }

    public void setHeightText(String height) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);

        attachedUser.updateLastModified();
        this.heightText = height;
        attachedUser.addChange(new Change("set height to " + height));
        mem.setNewObject(clone);
        attachedUser.getUndoStack().push(mem);
    }

    public String getWeightText() {
        return weightText;
    }

    public void setWeightText(String weight) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);

        attachedUser.updateLastModified();
        this.weightText = weight;
        attachedUser.addChange(new Change("set weight to " + weight));
        mem.setNewObject(clone);
        attachedUser.getUndoStack().push(mem);
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);

        attachedUser.updateLastModified();


        if (this.bloodType != bloodType) {
            this.bloodType = bloodType;
            attachedUser.addChange(new Change("Changed blood type to " + bloodType));
            mem.setNewObject(clone);
            attachedUser.getUndoStack().push(mem);
        }
    }

    public String getBirthGender() {
        return birthGender;
    }

    public void setBirthGender(String birthGender) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);

        attachedUser.updateLastModified();
        // Changes the default case where the gender identity is the same as the birth gender
        if (genderIdentity == null) {
            genderIdentity = this.birthGender;
        }
        this.birthGender = birthGender;
        attachedUser.addChange(new Change("Changed birth gender to " + birthGender));
        mem.setNewObject(clone);
        attachedUser.getUndoStack().push(mem);
    }

    public String getGenderIdentity() {
        return genderIdentity;
    }

    public void setGenderIdentity(String genderIdentity) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);

        attachedUser.updateLastModified();
        this.genderIdentity = genderIdentity;
        attachedUser.addChange(new Change("Changed birth Identity to " + genderIdentity));
        mem.setNewObject(clone);
        attachedUser.getUndoStack().push(mem);
    }

    public String getAlcoholConsumption() {
        return alcoholConsumption;
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);
        attachedUser.updateLastModified();
        this.alcoholConsumption = alcoholConsumption;
        attachedUser.addChange(new Change("Changed alcohol consumption to " + alcoholConsumption));
        mem.setNewObject(clone);
        attachedUser.getUndoStack().push(mem);
    }

    public boolean isSmoker() {
        return smoker;
    }

    public void setSmoker(boolean smoker) {
        Memento<User> mem = new Memento<>();
        User clone = attachedUser.clone();
        mem.setOldObject(clone);
        attachedUser.updateLastModified();
        this.smoker = smoker;
        attachedUser.addChange(new Change("Changed smoker status to " + smoker));
        mem.setNewObject(clone);
        attachedUser.getUndoStack().push(mem);
    }
}
