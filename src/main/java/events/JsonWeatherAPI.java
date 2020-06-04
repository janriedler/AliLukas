package events;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.util.Scanner;

class JsonWeatherAPI
{
    static String getWoeid(String city)
    {
        try {
            String inline = useAPI("https://www.metaweather.com/api/location/search/?query=" + city);
            if (!inline.equals("[]")) {
                String[] words = inline.split(",");
                String[] erg = words[2].split(":");
                return erg[1];
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String getWeather(String id, String datum) {
        String[] zahl = datum.split("-");
        datum = zahl[0] + "/" + zahl[1] + "/" + zahl[2];
        try {
            String inline = useAPI("https://www.metaweather.com/api/location/" + id + "/" + datum);
            if (!inline.equals("[]")) {
                String[] words = inline.split(",");
                String[] erg = words[1].split(":");
                System.out.println(erg[1]);
                return erg[1];
            } else {
                return "Wetter außerhalb Zeitraum";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String useAPI(String urlString) throws IOException {
        String inline = "";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responsecode = conn.getResponseCode();
        System.out.println("Response code is: " + responsecode);

        if(responsecode == 200) {
            Scanner sc = new Scanner(url.openStream());
            while(sc.hasNext()) {
                inline += sc.nextLine();
            }
            System.out.println("\nJSON Response in String format");
            System.out.println(inline);
            sc.close();
        }
        conn.disconnect();
        return inline;
    }
}