package seng302.Model;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import seng302.Directory;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public final class JsonHandler {

    //Initialise attributes
    private BufferedWriter fileWriter;

    /**
     * save the current users in the system to the filename given
     * Based on: https://stackoverflow.com/questions/14996663/is-there-a-standard-implementation-for-a-gson-joda-time-serialiser
     * @param users List of users to save
     * @throws IOException
     */
    public static void saveUsers(ArrayList<Donor> users) throws IOException {

        Files.createDirectories(Paths.get(Directory.JSON.directory()));
        File outFile = new File(Directory.JSON.directory()+"/donors.json");

        if (outFile.exists()){
            outFile.delete(); //purge old data before writing new data in
        }

        outFile.createNewFile(); //creates new file if donors does not exist
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, (JsonSerializer<DateTime>)
                (json, typeOfSrc, context) -> new JsonPrimitive(ISODateTimeFormat.dateTime().print(json))).create();
        FileWriter writer = new FileWriter(outFile);
        String usersString = gson.toJson(users);
        writer.write(usersString);
        writer.close();

    }

    /**
     * loads the users from a file and returns an Arraylist
     * @return list of donors present
     * @throws FileNotFoundException
     */

    public static ArrayList<Donor> loadUsers() throws FileNotFoundException {
        ArrayList<Donor> results = new ArrayList<>();
        File inFile = new File(Directory.JSON.directory() + "/donors.json");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        Reader reader =new FileReader(inFile);
        Donor[] donors = gson.fromJson(reader, Donor[].class);
        results.addAll(Arrays.asList(donors));
        return results;

    }


/*    public static void saveCommandLog(Stack<Command> commandStack) throws IOException {

        ArrayList<Command> commandList = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Command.class, new CommandAdapter());
        Gson gson = gsonBuilder.create();
        commandList = loadCommands();

        FileWriter writer = new FileWriter("./src/main/resources/log.json");

        while (!commandStack.isEmpty()) {
            commandList.add(commandStack.pop());
        }

        String usersString = gson.toJson(commandList);
        writer.write(usersString);
        writer.close();
    }

    public static ArrayList<Command> loadCommands() throws FileNotFoundException {
        ArrayList<Command> commandList = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Command.class, new CommandAdapter());
        Gson gson = gsonBuilder.create();

        JsonReader reader = new JsonReader(new FileReader("./src/main/resources/log.json"));

        ExecutedCommand[] data = gson.fromJson(reader, ExecutedCommand[].class);
        for (ExecutedCommand command : data) {
            commandList.add(command);
        }

        return commandList;
    }*/


}

