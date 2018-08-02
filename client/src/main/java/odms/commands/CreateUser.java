package odms.commands;

import odms.commons.model.User;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.DataHandler;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.utils.UserBridge;
import odms.view.IoHelper;
import okhttp3.OkHttpClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.time.LocalDate;


@Command(name = "user", description = "first name, last name, and dob are required. all other are optional and must be tagged")
public class CreateUser implements Runnable {

    @Option(names = {"-h",
            "help"}, required = false, usageHelp = true, description = "display a help message")
    private Boolean helpRequested = false;

    @Parameters(index = "0")
    private String firstName;

    @Parameters(index = "1")
    private String lastName;

    @Parameters(index = "2", description = "NHI 'ABC1234'")
    private String NHI;

    @Parameters(index = "3", description = "format 'yyyy-mm-dd'")
    private String dobString;

    @Option(names = {"-dod"}, description = "Date of death. same formatting as dob")
    private String dodString;

    @Option(names = {"-mn", "-middleName"}, description = "user's middle name")
    private String middleName;

    @Option(names = {"-pn", "-preferredName"}, description = "user's middle name")
    private String preferredName;

    @Option(names = {"-w", "-weight"}, description = "weight in kg e.g. 87.3")
    private String weight;

    @Option(names = {"-smo", "-smoker"}, description = "is this user a smoker. eg: 0 for false, 1 for true")
    private String smoker;

    @Option(names = {"-ac", "-alcoholConsumption"}, description = "alcohol consumption of this user. eg: 0 for None, 1 for Low, 2 for Normal, 3 for High")
    private String alcoholConsumption;

    @Option(names = {"-he", "-height"}, description = "height in m. e.g. 1.85")
    private String height;

    @Option(names = {"-bg", "-birthGender"}, description = "birth Gender.")
    private String birthGender;
    @Option(names = {"-g", "-gender"}, description = "gender.")
    private String gender;

    @Option(names = {"-b", "-bloodType"}, description = "blood type")
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

    @Option(names = {"-r", "-region"}, description = "Region (Address line 2)")
    private String region;

    private DataHandler dataHandler = new JsonHandler();

    public void run() {
        OkHttpClient client = new OkHttpClient();
        UserBridge userBridge = new UserBridge(client);

        AppController controller = AppController.getInstance();
        if (helpRequested) {
            System.out.println("help goes here"); //TODO: update help command -2/8
            return;
        }

        if(!AttributeValidation.validateNHI(NHI)){
            IoHelper.display("Invalid NHI");
            return;
        }

        if(!AttributeValidation.checkRequiredStringName(firstName)){
            IoHelper.display("Invalid first name");
            return;
        }

        LocalDate dob = IoHelper.readDate(dobString);
        if (!AttributeValidation.validateDateOfBirth(dob)) {
            IoHelper.display("Invalid Date of birth");
            return;
        }

        User user = new User(firstName, dob, NHI);
        boolean success = controller.addUser(new User(firstName, dob, NHI));
        if (!success) {
            IoHelper.display("An error occurred when creating registering the new user\n"
                    + "maybe a user with that NHI already exists?");
            return;
        }

        try {
            if (userBridge.getUser(user.getNhi()) != null) {
                IoHelper.display("User with this NHI "+user.getNhi()+" already exists");
                return;
            }
        } catch (IOException e){
            Log.warning("Unable to get user " + user.getNhi(), e);
        }

        if (middleName != null) {
            if (!AttributeValidation.checkRequiredStringName(middleName)) {
                IoHelper.display("Invalid middle name");
                return;
            }
            user.setMiddleName(middleName);
        }
        if(lastName != null){
            if(!AttributeValidation.checkRequiredStringName(lastName)){
                IoHelper.display("Invalid last name");
                return;
            }
            user.setLastName(lastName);
        }
        if (preferredName != null) {
            if (!AttributeValidation.checkRequiredStringName(preferredName)) {
                IoHelper.display("Invalid preferred name");
                return;
            }
            user.setPreferredFirstName(preferredName);
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

            } else if (smoker.equals("1")){
                user.setSmoker(true);

            } else {
                IoHelper.display("Invalid smoker");
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
            if (!AttributeValidation.checkString(city)) {
                IoHelper.display("Invalid city");
                return;
            }
            user.setCity(city);
        }
        if (country != null) {
            if (!AttributeValidation.checkString(country)) {
                IoHelper.display("Invalid country");
                return;
            }
            user.setCountry(country);
        }
        if (streetName != null) {
            if (!AttributeValidation.checkString(streetName)) {
                IoHelper.display("Invalid street name");
                return;
            }
            user.setStreetName(streetName);
        }
        if (number != null) {
            if (!AttributeValidation.checkString(number)) {
                IoHelper.display("Invalid street number");
                return;
            }
            user.setStreetNumber(number);
        }
        if (neighborhood != null) {
            if (!AttributeValidation.checkString(neighborhood)) {
                IoHelper.display("Invalid neighborhood");
                return;
            }
            user.setNeighborhood(neighborhood);
        }
        if (zipCode != null) {
            if (!AttributeValidation.checkString(zipCode)) {
                IoHelper.display("Invalid ZIP Code");
                return;
            }
            user.setZipCode(zipCode);
        }
        if (region != null) {
            if (!AttributeValidation.checkString(region)) {
                IoHelper.display("Invalid region");
                return;
            }
            user.setRegion(region);
        }

        IoHelper.display("User successfully registered with below details: ");
        IoHelper.display("ID number: "+user.hashCode());
        IoHelper.display(user.toString());
        controller.update(user);
        controller.saveUser(user);
    }

}
