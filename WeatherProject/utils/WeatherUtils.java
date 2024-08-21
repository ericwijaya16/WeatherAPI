package com.mycompany.testingprojectnb15.WeatherProject.utils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author ericw
 * 
 */
public class WeatherUtils {
    private static final String BASE_URL_FORECAST = "https://api.open-meteo.com/v1/forecast";
    private static final String BASE_URL_GEO = "https://api.opencagedata.com/geocode/v1/json";
    private static final String API_KEY_GEO = "2b5603bdcbda49619532395a708334f2";
    private static double LATITUDE = 0;
    private static double LONGITUDE = 0;
    private static HashMap<String, String> param = new HashMap();
    private static String input = "";
    private static HashMap<String, String> weatherParam = new HashMap<String, String>(){};
    private static List<String> VALID_VARIABLES = new ArrayList<>();
    private static HashMap<String, String> result = new HashMap();
    
    public WeatherUtils(String input, HashMap param) {
        this.input = input;
        this.param = param;
    }
    
    public static void fetchWeatherData() {
        try {
            result = new HashMap();
            loadParam();
            String[] location = null;
            if(input.equals("city")){
                System.out.print("Please Input Valid City Name: ");
                Scanner scanCity = new Scanner(System.in);
                input = scanCity.nextLine();
                try{
                    location = getCoordinates(input);
                    if(location[0].equals("")){
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                location = getLocation();
            }
            LATITUDE = Double.parseDouble(location[0]);
            LONGITUDE = Double.parseDouble(location[1]);
            result.put("address", getAddressFromLatLong(LATITUDE, LONGITUDE));
            
            LocalDateTime currentTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
            String formattedDateTime = currentTime.format(formatter);
            getInfo(formattedDateTime);
            
            System.out.println();
            System.out.println();
            for (String title : result.keySet()) {
                System.out.println(formatString(title) + ": " + result.get(title));
            }
            
            System.out.println("Waiting 5 seconds before going back to menu...");
            // Wait for 5 seconds before returning to the menu
            try {
                Thread.sleep(5000); // 5000 milliseconds = 5 seconds
                genSpace(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String formatString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Replace underscores with spaces
        String modifiedString = input.replace('_', ' ');

        // Capitalize the first character
        return modifiedString.substring(0, 1).toUpperCase() + modifiedString.substring(1).toLowerCase();
    }
    
    private static void genSpace(int input) {
        for (int i = 0; i < input; i++) {
            System.out.println();
        }
    }
    
    private static String[] getLocation() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://ipinfo.io/json")
                .build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();

        JSONObject json = new JSONObject(responseData);
        String loc = json.getString("loc");
        return loc.split(",");
    }
    
    private static void getInfo(String formattedDateTime) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String weatherCode = "";
        String[] variableWeather = param.get("hourly").split(",");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL_FORECAST).newBuilder();
        urlBuilder.addQueryParameter("latitude", String.valueOf(LATITUDE));
        urlBuilder.addQueryParameter("longitude", String.valueOf(LONGITUDE));
        for (String prm : param.keySet()) {
            urlBuilder.addQueryParameter(prm, param.get(prm));
        }

        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        //For debugging
//        System.out.println(url);
        // Use execute() to make the call synchronous
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseData = response.body().string();
            JSONObject json = new JSONObject(responseData);
            JSONObject hourly = json.getJSONObject("hourly");
            if (param.containsKey("current")) {
                JSONObject current = json.getJSONObject("current");
                weatherCode = Integer.toString(current.getInt("weather_code"));
            }
            JSONArray timeArray = hourly.getJSONArray("time");

            for (int i = 0; i < timeArray.length(); i++) {
                String tempTime = timeArray.getString(i);
                if (tempTime.equals(formattedDateTime)) {
                    result.put("time", tempTime.replace("T"," "));
                    for (String weatherVariable : variableWeather) {
                        JSONArray variableArray = hourly.getJSONArray(weatherVariable);
                        result.put(weatherVariable, variableArray.get(i).toString());
                    }
                    if (!weatherCode.equals("")) {
                        if (weatherParam.containsKey(weatherCode)) {
                            result.put("weather", weatherParam.get(weatherCode));
                        } else {
                            System.out.println("Invalid Weather Code!!!");
                            result.put("weather", "ERROR");
                        }
                    }
                    break;
                }
            }
        } else {
            System.out.println("Request failed: " + response.code());
        }
    }
    
    private static String getAddressFromLatLong(double lat, double lon) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String url = String.format(
            "https://nominatim.openstreetmap.org/reverse?lat=%f&lon=%f&format=json",
            lat, lon
        );
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        String responseData = response.body().string();

        JSONObject json = new JSONObject(responseData);
        if (json.has("address")) {
            JSONObject address = json.getJSONObject("address");
            StringBuilder fullAddress = new StringBuilder();

            // Construct a more human-readable address
            if (address.has("village")) fullAddress.append(address.getString("village")).append(", ");
            if (address.has("municipality")) fullAddress.append(address.getString("municipality")).append(", ");
            if (address.has("county")) fullAddress.append(address.getString("county")).append(", ");
            if (address.has("region")) fullAddress.append(address.getString("region")).append(", ");
            if (address.has("country")) fullAddress.append(address.getString("country"));

            return fullAddress.toString();
        } else {
            return "No address found for the provided coordinates.";
        }
    }
    
    public static String[] getCoordinates(String city) throws Exception {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL_GEO + "?q=" + city + "&key=" + API_KEY_GEO;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new Exception("Unexpected response code: " + response.code());
            }

            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            if (jsonObject.getJSONArray("results").length() == 0) {
                System.out.println("City not found: " + city);
                return new String[]{""};
            }

            JSONObject results = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry");
            String latitude = Double.toString(results.getDouble("lat"));
            String longitude = Double.toString(results.getDouble("lng"));

            return new String[]{latitude, longitude};
        } catch (Exception e) {
            throw new Exception("Error fetching coordinates: " + e.getMessage(), e);
        }
    }
    
    public static List<String> loadValidVariables() {
        List<String> validVar = new ArrayList<>();
        validVar = Arrays.asList(
            "temperature_2m", "relative_humidity_2m", "dew_point_2m", "apparent_temperature",
            "pressure_msl", "surface_pressure", "cloud_cover", "cloud_cover_low",
            "cloud_cover_mid", "cloud_cover_high", "wind_speed_10m", "wind_speed_80m",
            "wind_speed_120m", "wind_speed_180m", "wind_direction_10m", "wind_direction_80m",
            "wind_direction_120m", "wind_direction_180m", "wind_gusts_10m",
            "shortwave_radiation", "direct_radiation", "direct_normal_irradiance",
            "diffuse_radiation", "global_tilted_irradiance", "sunshine_duration",
            "vapour_pressure_deficit", "evapotranspiration", "et0_fao_evapotranspiration",
            "precipitation", "snowfall", "weather_code", "freezing_level_height",
            "soil_temperature_0_to_10cm", "soil_temperature_10_to_40cm",
            "soil_temperature_40_to_100cm", "soil_temperature_100_to_200cm"
        );
        return validVar;
    }
    
    private static void loadParam() {
        weatherParam = new HashMap();
        weatherParam.put("0", "Clear Sky");
        weatherParam.put("1", "Mainly Clear");
        weatherParam.put("2", "Mainly Clear");
        weatherParam.put("3", "Mainly Clear");
        weatherParam.put("45", "Fog and Depositing Rime Fog");
        weatherParam.put("48", "Fog and Depositing Rime Fog");
        weatherParam.put("51", "Drizzle: Light Intensity");
        weatherParam.put("53", "Drizzle: Moderate Intensity");
        weatherParam.put("55", "Drizzle: Dense Intensity");
        weatherParam.put("56", "Freezing Drizzle: Light Intensity");
        weatherParam.put("57", "Freezing Drizzle: Dense Intensity");
        weatherParam.put("61", "Rain: Slight Intensity");
        weatherParam.put("63", "Rain: Moderate Intensity");
        weatherParam.put("65", "Rain: Heavy Intensity");
        weatherParam.put("66", "Freezing Rain: Light Intensity");
        weatherParam.put("67", "Freezing Rain: Heavy Intensity");
        weatherParam.put("71", "Snow Fall: Slight Intensity");
        weatherParam.put("73", "Snow Fall: Moderate Intensity");
        weatherParam.put("75", "Snow Fall: Heavy Intensity");
        weatherParam.put("77", "Snow Grains");
        weatherParam.put("80", "Rain Showers: Slight Intensity");
        weatherParam.put("81", "Rain Showers: Moderate Intensity");
        weatherParam.put("82", "Rain Showers: Violent Intensity");
        weatherParam.put("85", "Snow Showers: Slight Intensity");
        weatherParam.put("86", "Snow Showers: Heavy Intensity");
        weatherParam.put("95", "Thunderstorm: Slight or Moderate");
        weatherParam.put("96", "Thunderstorm with Slight Hail");
        weatherParam.put("99", "Thunderstorm with Heavy Hail");
    }       
}
