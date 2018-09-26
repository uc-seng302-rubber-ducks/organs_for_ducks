package odms.commands;

import odms.bridge.UserBridge;
import odms.commons.model.User;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


@Command(name = "user", description = "nhi, first name and dob are required. All others are optional and must be tagged")
public class CreateUser implements Runnable {

    @Option(names = {"-h",
            "help"}, usageHelp = true, description = "Display a help message")
    private Boolean helpRequested = false;

    @Parameters(index = "0", description = "nhi 'ABC1234'")
    private String nhi;

    @Parameters(index = "1")
    private String firstName;

    @Parameters(index = "2", description = "Date of birth in the format 'yyyy-mm-dd'")
    private String dobString;

    @Option(names = {"-dod"}, description = "Date of death with the same formatting as dob")
    private String dodString;

    @Option(names = {"-m", "-middleName"}, description = "User's middle name")
    private String middleName;

    @Option(names = {"-pn", "-preferredName"}, description = "User's preferred first name")
    private String preferredName;

    @Option(names = {"-l", "-lastName"}, description = "User's last name")
    private String lastName;

    @Option(names = {"-w", "-weight"}, description = "Weight in kg e.g. 87.3")
    private String weight;

    @Option(names = {"-smo", "-smoker"}, description = "Is this user a smoker\neg: 0 for false, 1 for true")
    private String smoker;

    @Option(names = {"-ac", "-alcoholConsumption"}, description = "Alcohol consumption of this user\neg: 0 for None, 1 for Low, 2 for Normal, 3 for High")
    private String alcoholConsumption;

    @Option(names = {"-he", "-height"}, description = "Height in m. e.g. 1.85")
    private String height;

    @Option(names = {"-bg", "-birthGender"}, description = "The gender the user was born as")
    private String birthGender;

    @Option(names = {"-g", "-genderIdentity"}, description = "The gender the user identifies as")
    private String gender;

    @Option(names = {"-b", "-bloodType"}, description = "The blood type of the user")
    private String bloodType;

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

    @Option(names = {"-r", "-region"}, description = "Current address region")
    private String region;

    public void run() {

        AppController controller = AppController.getInstance();
        UserBridge userBridge = controller.getUserBridge();
        if (helpRequested) {
            return;
        }

        if (!AttributeValidation.validateNHI(nhi)) {
            IoHelper.display("Invalid nhi");
            return;
        } else if (userBridge.getExists(nhi)) {
            IoHelper.display("A user with that nhi already exists");
            return;
        }

        if (!AttributeValidation.checkRequiredStringName(firstName.replaceAll("_", " "))) {
            IoHelper.display("Invalid first name");
            return;
        }

        LocalDate dob = IoHelper.readDate(dobString);
        if (!AttributeValidation.validateDateOfBirth(dob)) {
            IoHelper.display("Invalid Date of birth");
            return;
        }

        User user = new User(firstName.replaceAll("_", " "), dob, nhi);
        boolean success = controller.addUser(new User(firstName.replaceAll("_", " "), dob, nhi));
        if (!success) {
            IoHelper.display("An error occurred when creating registering the new user\n"
                    + "maybe a user with that nhi already exists?");
            return;
        }

        try {
            if (userBridge.getUser(user.getNhi()) != null) {
                IoHelper.display("User with this nhi " + user.getNhi() + " already exists");
                return;
            }
        } catch (IOException e) {
            Log.warning("Unable to get user " + user.getNhi(), e);
        }

        if (middleName != null) {
            if (!AttributeValidation.checkRequiredStringName(middleName.replaceAll("_", " "))) {
                IoHelper.display("Invalid middle name");
                return;
            }
            user.setMiddleName(middleName.replaceAll("_", " "));
        }

        if (lastName != null) {
            if (!AttributeValidation.checkRequiredStringName(lastName.replaceAll("_", " "))) {
                IoHelper.display("Invalid last name");
                return;
            }
            user.setLastName(lastName.replaceAll("_", " "));
        }

        if (preferredName != null) {
            if (!AttributeValidation.checkRequiredStringName(preferredName.replaceAll("_", " "))) {
                IoHelper.display("Invalid preferred name");
                return;
            }
            user.setPreferredFirstName(preferredName.replaceAll("_", " "));
        }

        if (dodString != null) {
            LocalDate dod = IoHelper.readDate(dodString);
            if (!AttributeValidation.validateDateOfDeath(dob, dod)) {
                IoHelper.display("Invalid Date of death");
                return;
            }
            user.setDateOfDeath(IoHelper.readDate(dodString));
        }

        if (weight != null) {
            Double w = AttributeValidation.validateDouble(weight);
            if (w == -1) {
                IoHelper.display("Invalid weight");
                return;
            }
            user.setWeight(w);
        }

        if (height != null) {
            Double h = AttributeValidation.validateDouble(height);
            if (h == -1) {
                IoHelper.display("Invalid height");
                return;
            }
            user.setHeight(h);
        }

        if (birthGender != null) {
            if (!AttributeValidation.validateGender(birthGender)) {
                IoHelper.display("Invalid birthGender");
                return;
            }
            user.setBirthGender(birthGender);
        }

        if (gender != null) {
            if (!AttributeValidation.validateGender(gender)) {
                IoHelper.display("Invalid gender");
                return;
            }
            user.setGenderIdentity(gender);
        }

        if (bloodType != null) {
            if (!AttributeValidation.validateBlood(bloodType)) {
                IoHelper.display("Invalid blood type");
                return;
            }
            user.setBloodType(bloodType);
        }

        if (smoker != null) {
            if (smoker.equals("0")) {
                user.setSmoker(false);

            } else if (smoker.equals("1")) {
                user.setSmoker(true);

            } else {
                IoHelper.display("Invalid smoker value");
                return;
            }
        }

        if (alcoholConsumption != null) {
            if (!AttributeValidation.validateAlcoholLevel(alcoholConsumption)) {
                IoHelper.display("Invalid alcohol consumption");
                return;
            }
            user.setAlcoholConsumption(alcoholConsumption);
        }

        if (city != null) {
            if (!AttributeValidation.checkString(city.replaceAll("_", " "))) {
                IoHelper.display("Invalid city");
                return;
            }
            user.setCity(city.replaceAll("_", " "));
        }

        if (country != null) {
            List<String> allowedCountries = controller.getAllowedCountries();
            if (allowedCountries.contains(country.replaceAll("_", " "))) {
                user.setCountry(country.replaceAll("_", " "));
            } else {
                IoHelper.display(country + " is not one of the allowed countries\n" +
                        "For a list of the allowed countries use the command 'view countries'");
                return;
            }
        }

        if (streetName != null) {
            if (!AttributeValidation.checkString(streetName.replaceAll("_", " "))) {
                IoHelper.display("Invalid street name");
                return;
            }
            user.setStreetName(streetName.replaceAll("_", " "));
        }

        if (number != null) {
            if (!AttributeValidation.checkString(number)) {
                IoHelper.display("Invalid street number");
                return;
            }
            user.setStreetNumber(number);
        }

        if (neighborhood != null) {
            if (!AttributeValidation.checkString(neighborhood.replaceAll("_", " "))) {
                IoHelper.display("Invalid neighborhood");
                return;
            }
            user.setNeighborhood(neighborhood.replaceAll("_", " "));
        }

        if (zipCode != null) {
            if (!AttributeValidation.checkString(zipCode)) {
                IoHelper.display("Invalid ZIP Code");
                return;
            }
            user.setZipCode(zipCode);
        }

        if (region != null) {
            if (!AttributeValidation.checkString(region.replaceAll("_", " "))) {
                IoHelper.display("Invalid region");
                return;
            } else if (user.getCountry().equals("New Zealand") && !controller.getAllNZRegion().contains(region.replaceAll("_", " "))) {
                IoHelper.display("A New Zealand region must be given");
                return;
            }
            user.setRegion(region.replaceAll("_", " "));
        }

        IoHelper.display("User successfully registered with below details: ");
        IoHelper.display("ID number: " + user.hashCode());
        IoHelper.display(user.toString());
        controller.update(user);
        controller.saveUser(user);
    }

}
