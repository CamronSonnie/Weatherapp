import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class weatherapp {

    public static void main(String[] args) {
        // Get user input for zip code
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a 5 digit zip code: ");
        String input = scanner.nextLine();

        // Validate zip code input
        if (input.length() != 5) {
            System.out.println("Input must be 5 digits");
            return;
        }
        
        String apiKey = "6755c50f9fa4beafbaa736835855e713";  
        try {
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?zip=" + input + ",US&appid=" + apiKey;

            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(responseBuilder.toString());

                // Check if the "city" object is present
                if (response.has("city")) {
                    JSONObject cityObject = response.getJSONObject("city");
                    
                    // Check if the "name" field is present in the "city" object
                    if (cityObject.has("name")) {
                        String cityName = cityObject.getString("name");
                        System.out.println("The Weather in " + cityName + " for the next 5 days:");

                        JSONArray weatherList = response.getJSONArray("list");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE MMMM dd, yyyy h:mm a");

                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_YEAR, 1); // Start from tomorrow

                        for (int i = 0; i < weatherList.length(); i++) {
                            JSONObject weatherInfo = weatherList.getJSONObject(i);
                            long timestamp = weatherInfo.getLong("dt") * 1000;  // Convert seconds to milliseconds
                            Date date = new Date(timestamp);

                            // Check if the current time is 8:00 AM
                            calendar.setTime(date);
                            if (calendar.get(Calendar.HOUR_OF_DAY) == 8 && calendar.get(Calendar.MINUTE) == 0) {
                                System.out.println(dateFormat.format(date));
                                System.out.println("      Weather:  " + weatherInfo.getJSONArray("weather").getJSONObject(0).getString("main"));
                                System.out.println("      Ave Temp: " + getAveTemp(weatherInfo) + "F");
                                System.out.println("      Ave Wind: " + getAveWind(weatherInfo) + " mph");
                                System.out.println("      Ave Gust: " + getAveGust(weatherInfo) + " mph\n");
                            }

                            // Check if we have recorded data for the next 3 days
                            if (i > 0 && isNextThreeDaysComplete(calendar.getTime(), weatherList.getJSONObject(i - 1).getLong("dt") * 1000)) {
                                break;
                            }
                        }
                    } else {
                        System.out.println("Error: City name not found in the API response");
                    }
                } else {
                    System.out.println("Error: City object not found in the API response");
                }
            } else {
                System.out.println("Error fetching weather data (HTTP " + responseCode + ")");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getAveTemp(JSONObject weatherInfo) {
        double tempTotal = weatherInfo.getJSONObject("main").getDouble("temp");
        return (int) Math.round((tempTotal - 273.15) * 9 / 5 + 32);
    }

    public static int getAveWind(JSONObject weatherInfo) {
        double windSpeed = weatherInfo.getJSONObject("wind").getDouble("speed");
        return (int) Math.round(windSpeed * 2.23694);
    }

    public static int getAveGust(JSONObject weatherInfo) {
        double gustSpeed = weatherInfo.getJSONObject("wind").getDouble("gust");
        return (int) Math.round(gustSpeed * 2.23694);
    }

    private static boolean isNextThreeDaysComplete(Date currentDate, long timestamp) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(currentDate);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(timestamp));

        int diffInDays = (int) ((cal2.getTime().getTime() - cal1.getTime().getTime()) / (1000 * 60 * 60 * 24));
        return diffInDays >= 3;
    }
}
