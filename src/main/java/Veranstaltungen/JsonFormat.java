package Veranstaltungen;

import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

class JsonFormat
{
    public static String getWoeid(String stadt)
    {
        //inline will store the JSON data streamed in string format
        String inline = "";

        try
        {
            URL url = new URL("https://www.metaweather.com/api/location/search/?query=" + stadt);
            //Parse URL into HttpURLConnection in order to open the connection in order to get the JSON data
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //Set the request to GET or POST as per the requirements
            conn.setRequestMethod("GET");
            //Use the connect method to create the connection bridge
            conn.connect();
            //Get the response status of the Rest API
            int responsecode = conn.getResponseCode();
            System.out.println("Response code is: " +responsecode);

            //Iterating condition to if response code is not 200 then throw a runtime exception
            //else continue the actual process of getting the JSON data
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: " +responsecode);
            else
            {
                //Scanner functionality will read the JSON data from the stream
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNext())
                {
                    inline+=sc.nextLine();
                }
                System.out.println("\nJSON Response in String format");
                System.out.println(inline);
                //Close the stream when reading the data has been finished
                sc.close();
            }

            if (inline.equals("[]")){
                conn.disconnect();
                return "noloc";
            } else {
                String[] words = inline.split(",");
                String[] erg = words[2].split(":");
                conn.disconnect();
                return erg[1];
            }



            //Disconnect the HttpURLConnection stream

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return "error";

    }



    public static String getWetter (String id, String datum) {
        //inline will store the JSON data streamed in string format
        String inline = "";
        String[] zahl = datum.split("-");
        datum = zahl[0] + "/" + zahl[1] + "/" + zahl[2];

        try {
            URL url = new URL("https://www.metaweather.com/api/location/" + id + "/" + datum);
            System.out.println(url);
            //Parse URL into HttpURLConnection in order to open the connection in order to get the JSON data
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //Set the request to GET or POST as per the requirements
            conn.setRequestMethod("GET");
            //Use the connect method to create the connection bridge
            conn.connect();
            //Get the response status of the Rest API
            int responsecode = conn.getResponseCode();
            System.out.println("Response code is: " + responsecode);

            //Iterating condition to if response code is not 200 then throw a runtime exception
            //else continue the actual process of getting the JSON data
            if (responsecode != 200)
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            else {
                //Scanner functionality will read the JSON data from the stream
                Scanner sc = new Scanner(url.openStream());
                while (sc.hasNext()) {
                    inline += sc.nextLine();
                }
                System.out.println("\nJSON Response in String format");
                System.out.println(inline);
                //Close the stream when reading the data has been finished
                sc.close();
            }

            if (!inline.equals("[]")) {

                String[] words = inline.split(",");
                String[] erg = words[1].split(":");
                System.out.println(erg[1]);
                conn.disconnect();
                return erg[1];
            } else {
                conn.disconnect();
                return "Wetter außerhalb Zeitraum";
            }




            //Disconnect the HttpURLConnection stream

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";

    }


    //prüft ob Datum innerhelb der nächten Woche liegt (Kann noch verbessert werden)
    static boolean checkTime(String datum){
        String inline = "";
        String[] zahl = datum.split("-");
        datum = zahl[1] + "/" + zahl[2] + "/" + zahl[0];
        System.out.println(datum);
        return ZonedDateTime
                .now()                           // Captures current moment as seen by the wall-clock time of the JVM’s current default time zone. Better to pass the optional `ZoneId` argument to specify explicitly the desired/expected time zone.
                .minusWeeks( 1 )
                .isBefore(
                        LocalDateTime
                                .parse(
                                        datum + " 00:01 PM" ,
                                        DateTimeFormatter.ofPattern( "MM/dd/uuuu hh:mm a" , Locale.US )
                                )
                                .atZone(
                                        ZoneId.systemDefault()   // Better to pass explicitly the time zone known to have been intended for this input. See discussion below.
                                )
                );
    }

}