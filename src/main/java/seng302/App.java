package seng302;

import java.util.Date;
import seng302.Model.Donor;

public class App
{
    public static void main( String[] args )
    {
        Donor d = new Donor("bob", new Date(1,1,2018));
        System.out.println(d);
    }
}
