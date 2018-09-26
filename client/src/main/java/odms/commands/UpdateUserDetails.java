package odms.commands;

import odms.commons.model.User;
import odms.commons.utils.AttributeValidation;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.time.LocalTime;

@Command(name = "details", description = "The current NHI is required to identify the the user. All other tags will update values")
public class UpdateUserDetails implements Runnable {


    @CommandLine.Parameters(index = "0")
    private String originalNhi;

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "Display a help message")
    private Boolean helpRequested = false;

    @Option(names = {"-f", "-fname"})
    private String firstName;

    @Option(names = {"-pn", "-pname"}, description = "The preferred first name")
    private String preferredName;

    @Option(names = {"-m", "-mname"})
    private String middleName;

    @Option(names = {"-l", "-lname"})
    private String lastName;

    @Option(names = {"-id", "-nhi", "-NHI", "-newNHI", "-newnhi"}, description = "The new NHI to replace the existing one")
    private String newNHI;

    @Option(names = {"-dob"}, description = "Date of birth. Format 'yyyy-mm-dd'")
    private String dobString;

    @Option(names = {"-dod"}, description = "Date of death. Format 'yyyy-mm-dd'  *OR*  Can pass in 'null' to remove date of death")
    private String dodString;

    @Option(names = {"-w", "-weight"}, description = "weight in kg e.g. 87.3")
    private double weight = -1;

    @Option(names = {"-he", "-height"}, description = "height in m. e.g. 1.85")
    private double height = -1;

    @Option(names = {"-g", "-birthgender"}, description = "Users birth gender")
    private String gender;

    @Option(names = {"-gi", "-genderIdentity"}, description = "Gender that the user identifies by")
    private String genderIdentity;

    @Option(names = {"-b", "-bloodType"}, description = "blood type")
    private String bloodType;

    @Option(names = {"-smo", "-smoker"}, description = "Is this user a smoker\neg: 0 for false, 1 for true")
    private String smoker;

    @Option(names = {"-ac", "-alcoholConsumption"}, description = "Alcohol consumption of this user\neg: 0 for None, 1 for Low, 2 for Normal, 3 for High")
    private String alcoholConsumption;

    @Option(names = {"-hp", "-homePhone"}, description = "Home phone number")
    private String homePhone;

    @Option(names = {"-cp", "-cellPhone"}, description = "Cell phone number")
    private String cellPhone;

    @Option(names = {"-e", "-email"}, description = "email")
    private String email;

    @Option(names = {"-c", "-city"}, description = "Current address city")
    private String city;

    @Option(names = {"-n", "-number"}, description = "Current address number")
    private String number;

    @Option(names = {"-s", "-streetName"}, description = "Current address street name")
    private String streetName;

    @Option(names = {"-z", "-zipCode"}, description = "Current address zipCode")
    private String zipCode;

    @Option(names = {"-co", "-country"}, description = "Current address country")
    private String country;

    @Option(names = {"-ne", "-neighborhood"}, description = "Current address neighborhood")
    private String neighborhood;

    @Option(names = {"-r", "-region"}, description = "Region (Address line 2)")
    private String region;

    @Option(names = {"-tod", "-timeOfDeath"}, description = "Time of death. Format 'hh:mm'")
    private String timeOfDeath;

    @Option(names = {"-dc", "-deathCity"}, description = "City of death")
    private String cityOfDeath;

    @Option(names = {"-dr", "-deathRegion"}, description = "Region of death")
    private String regionOfDeath;

    @Option(names = {"-dco", "-deathCountry"}, description = "Country of death")
    private String countryOfDeath;

    private AppController controller = AppController.getInstance();

    @Override
    public void run() {
        Boolean changed;
        if (helpRequested) {
            IoHelper.display("help goes here");
            return;
        }
        User user;
        try {
            user = controller.getUserBridge().getUser(originalNhi);
        } catch (IOException e) {
            IoHelper.display("Donor could not be found");
            return;
        }
        changed = IoHelper.updateName(user, firstName, lastName);

        if (preferredName != null) {
            user.setMiddleName(preferredName.replaceAll("_", " "));
            changed = true;
        }

        if (middleName != null) {
            user.setMiddleName(middleName.replaceAll("_", " "));
            changed = true;
        }

        if (dobString != null) {
            LocalDate newDate = IoHelper.readDate(dobString);
            if (newDate != null) {
                user.setDateOfBirth(newDate);
                changed = true;
            }
        }

        if (dodString != null) {
            if (dodString.equals("null")) { //Remove moment of death.
                user.setMomentOfDeath(null);
                changed = true;
            } else {
                LocalDate newDate = IoHelper.readDate(dodString);
                if (newDate != null) {
                    user.setDateOfDeath(newDate);
                    changed = true;
                }
            }
        }
        if (weight != -1) {
            user.setWeight(weight);
            changed = true;
        }
        if (height != -1) {
            user.setHeight(height);
            changed = true;
        }
        if (gender != null) {
            user.setBirthGender(gender);
            changed = true;
        }
        if (genderIdentity != null) {
            user.setGenderIdentity(genderIdentity);
            changed = true;
        }
        if (bloodType != null) {
            user.setBloodType(bloodType);
            changed = true;
        }

        if (smoker != null) {
            switch (smoker) {
                case "0": user.setSmoker(false); break;
                case "1": user.setSmoker(true); break;
                default: IoHelper.display("Invalid smoker value"); return;
            }
        }

        if (alcoholConsumption != null) {
            if (!AttributeValidation.validateAlcoholLevel(alcoholConsumption)) {
                IoHelper.display("Invalid alcohol consumption value");
                return;
            }
            user.setAlcoholConsumption(alcoholConsumption);
        }

        if (city != null) {
            user.setCity(city.replaceAll("_", " "));
            changed = true;
        }
        if (country != null) {
            List<String> allowedCountries = controller.getAllowedCountries();
            if (allowedCountries.contains(country.replaceAll("_", " "))) {
                user.setCountry(country.replaceAll("_", " "));
                changed = true;
            } else {
                IoHelper.display(country + " is not one of the allowed countries\n" +
                        "For a list of the allowed countries use the command 'view countries'");
            }
        }
        if (streetName != null) {
            user.setStreetName(streetName.replaceAll("_", " "));
            changed = true;
        }
        if (number != null) {
            user.setStreetNumber(number);
            changed = true;
        }
        if (neighborhood != null) {
            user.setNeighborhood(neighborhood.replaceAll("_", " "));
            changed = true;
        }

        if (region != null) {
            if (!AttributeValidation.checkString(region.replaceAll("_", " "))) {
                IoHelper.display("Invalid region");
                return;
            } else if (user.getCountry().equals("New Zealand") &&
                    !controller.getAllNZRegion().contains(region.replaceAll("_", " "))) {
                IoHelper.display("A New Zealand region must be given");
                return;
            }
            user.setRegion(region.replaceAll("_", " "));
            changed = true;
        }
        if (newNHI != null) {
            boolean exists = controller.getUserBridge().getExists(newNHI);
            if (exists) {
                IoHelper.display("User with this nhi already exists");
                return;
            }
            user.setNhi(newNHI);
            changed = true;
        }

        if (homePhone != null) {
            user.setHomePhone(homePhone);
            changed = true;
        }
        if (cellPhone != null) {
            user.setCellPhone(cellPhone);
            changed = true;
        }
        if (email != null) {
            user.setEmail(email);
            changed = true;
        }

        if (timeOfDeath != null) {
            if (!AttributeValidation.validateTimeString(timeOfDeath)) {
                IoHelper.display("Please enter a valid time format: hh:mm");
            } else if (user.getDateOfDeath() == null) {
                IoHelper.display("No date of death for user found. Date of death set to current day with time set as specified");
                user.setMomentOfDeath(user.getDeathDetails().createMomentOfDeath(LocalDate.now(), LocalTime.parse(timeOfDeath)));
                changed = true;
            } else {
                user.setMomentOfDeath(user.getDeathDetails().createMomentOfDeath(user.getDateOfDeath(), LocalTime.parse(timeOfDeath)));
                changed = true;
            }
        }

        if (cityOfDeath != null) {
            user.setDeathCity(cityOfDeath.replaceAll("_", " "));
            changed = true;
        }

        if (regionOfDeath != null) {
            user.setDeathRegion(regionOfDeath.replaceAll("_", " "));
            changed = true;
        }

        if (countryOfDeath != null) {
            user.setDeathCountry(countryOfDeath.replaceAll("_", " "));
            changed = true;
        }

        if (changed) {
            controller.update(user);
            controller.saveUser(user);
            IoHelper.display("Successfully updated user:"+ originalNhi);
        }
    }

    public void setAppController(AppController appController){
        this.controller = appController;
    }
}
