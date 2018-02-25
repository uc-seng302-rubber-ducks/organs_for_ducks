package seng302;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;

public class App
{
    public static void main( String[] args ) {
        Donor d = new Donor("bob", new Date(1, 1, 2018));
        System.out.println(d);
        ArrayList<Donor> ds = new ArrayList<Donor>();
        ds.add(d);

        try {
            JsonWriter.saveCurrentDonorState(ds);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
