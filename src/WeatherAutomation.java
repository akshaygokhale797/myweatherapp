import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.HashMap;
import java.util.Scanner;

//Build path is a list of all the resources that are required to build a
    //java project, including source files, class files, libraries, 
    //and other dependencies, to compile and build the java project

//Buffered Reader reads text from character input stream, buffering chars
    //so as to provide for efficient reading

//Input Stream Reader is bridge from byte streams to character streams (decodss)

//HttpUrlConnection is used to make a single request but the underlying network 
    //connection to the HTTP server may be transparently shared by other instances

//Use Geocoding API to turn city name into coordinates and pass coordinates to next AP call

public class WeatherAutomation {
    // Define API URL and your API key
    private static final String API_KEY = "fc994cc560c5e83d066f236e4ef9630a";
    private static final String GEO_API_URL = "http://api.openweathermap.org/geo/1.0/direct?q=%s,%s,US&limit=1&appid=" + API_KEY;
    // %s is a format specifier for the String.format method - represents any data type and outputs a string value 
    private static final String WEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=" + API_KEY;

    public static JSONObject fetchCoordinates(String city, String state) {
        try {
            URL url = new URL(String.format(GEO_API_URL, city, state));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  //This method opens a connection to the specified URL
                // Must cast the URLConnection to a HttpURLConnection to allow for Http-specific functionality
            conn.setRequestMethod("GET");   // GET fetches data from server 
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            JSONArray jsonArray = new JSONArray(response.toString());
            if (jsonArray.length() > 0) {
                return jsonArray.getJSONObject(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject fetchWeatherData(double lat, double lon) {
        try {
            URL url = new URL(String.format(WEATHER_API_URL, lat, lon));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return new JSONObject(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static double kelvinToFahrenheit(double kelvin) {
        return (kelvin - 273.15) * 9/5 + 32;
    }

    public static HashMap<String, Object> parseWeatherData(JSONObject json) {
        HashMap<String, Object> weatherData = new HashMap<>();
        
        weatherData.put("city", json.getString("name"));
        JSONObject main = json.getJSONObject("main");
        weatherData.put("temperature", (int) kelvinToFahrenheit(main.getDouble("temp")));
        weatherData.put("feels_like", (int) kelvinToFahrenheit(main.getDouble("feels_like")));
        weatherData.put("temp_min", (int) kelvinToFahrenheit(main.getDouble("temp_min")));
        weatherData.put("temp_max", (int) kelvinToFahrenheit(main.getDouble("temp_max")));
        weatherData.put("pressure", main.getInt("pressure"));
        weatherData.put("humidity", main.getInt("humidity"));

        JSONArray weatherArray = json.getJSONArray("weather");
        if (weatherArray.length() > 0) {
            JSONObject weather = weatherArray.getJSONObject(0);
            weatherData.put("main", weather.getString("main"));
            weatherData.put("description", weather.getString("description"));
        }

        JSONObject wind = json.getJSONObject("wind");
        weatherData.put("wind_speed", wind.getDouble("speed"));
        weatherData.put("wind_deg", wind.getInt("deg"));

        return weatherData;
    }

}
