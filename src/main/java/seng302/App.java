package seng302;


import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import seng302.Model.Donor;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

@Deprecated
public class App
{
    public static void main( String[] args ) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dob = new Date("01/01/1990");
        Donor d = new Donor("Bob", dob);
        System.out.println(d);
        ArrayList<Donor> ds = new ArrayList<Donor>();
        ds.add(d);
        ds.add(d);

        try {
            JsonWriter.saveCurrentDonorState(ds);
            System.out.println("file successfully created");
        } catch (IOException e) {
            e.printStackTrace();

        }

            ArrayList<Donor> donors;
            donors = JsonReader.importJsonDonors();
            for (Donor da : donors){
                System.out.println(da);
            }
    }
}
