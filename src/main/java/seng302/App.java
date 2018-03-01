package seng302;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

import static seng302.JsonReader.importJsonDonors;

public class App
{
    public static void main( String[] args ) {
        Donor d = new Donor("Bob", new Date(1,1,1990));
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
        try{
            ArrayList<Donor> donors;
            donors = JsonReader.importJsonDonors();
            for (Donor da : donors){
                System.out.println(da);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
