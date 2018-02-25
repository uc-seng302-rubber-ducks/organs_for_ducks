package seng302;

import org.joda.time.DateTime;

import java.util.Date;

public class App
{
    public static void main( String[] args )
    {
        Donor d = new Donor("bob", new Date(1,1,2018));
        System.out.println(d);
    }
}
