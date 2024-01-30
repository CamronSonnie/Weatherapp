import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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

        // Fetch weather data from OpenWeatherMap API
        String apiKey = "6755c50f9fa4beafbaa736835855e713";  // Replace with your OpenWeatherMap API key
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?zip= " + input + ",US&appid=" + apiKey;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(apiUrl))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();
            if (responseCode == 200) {
                // Parse JSON response using Scanner
                Scanner jsonScanner = new Scanner(response.body());
                String jsonResponse = jsonScanner.useDelimiter("\\A").next();
                jsonScanner.close();

                Object json = new Object();
                String cityName = json.getJSONObject("city").getString("name");

                // Process and display weather information
                String output = "\nThe Weather in " + cityName + " is:\n\n";
                List<int[]> aves = getAves(json);

                Date usedDate = new Date(0);
                int count = 0;
                JSONArray weatherList = json.getJSONArray("list");

                for (int i = 0; i < weatherList.length(); i++) {
                    if (count == 3) {
                        break;
                    }

                    JSONObject weatherInfo = weatherList.getJSONObject(i);
                    long timestamp = weatherInfo.getLong("dt") * 1000;  // Convert seconds to milliseconds
                    Date date = new Date(timestamp);

                    if (date == usedDate) {
                        continue;
                    }

                    usedDate = date;

                    output += String.format("   %tA %tB %td, %tY\n", date, date, date, date);
                    output += "      Weather:  " + weatherInfo.getJSONArray("weather").getJSONObject(0).getString("main") + "\n";
                    output += "      Ave Temp: " + aves.get(count)[0] + "F\n";
                    output += "      Ave Wind: " + aves.get(count)[1] + " mph\n";
                    output += "      Ave Gust: " + aves.get(count)[2] + " mph\n";
                    output += "\n";

                    count++;
                }

                System.out.println(output);
            } else {
                System.out.println("Error fetching weather data (HTTP " + responseCode);
            }

        } catch (IOException | InterruptedException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

   
	private static List<int[]> getAves(JSONObject json) {
    	
    	Date currentDate = new Date(json.getJSONArray("list").getJSONObject(0).getLong("dt") * 1000);
        double tempTotal = 0;
        double windTotal = 0;
        double gustTotal = 0;
        int count = 0;
        List<int[]> result = new ArrayList<>();
        int repeat = 0;

        JSONArray listArray = json.getJSONArray("list");

        for (int i = 0; i < listArray.length(); i++) {
            if (repeat == 3) {
                break;
            }

            JSONObject weatherInfo = listArray.getJSONObject(i);
            Date date = new Date(weatherInfo.getLong("dt") * 1000);

            if (date.getDate() == currentDate.getDate()) {
                tempTotal += weatherInfo.getJSONObject("main").getDouble("temp");
                windTotal += weatherInfo.getJSONObject("wind").getDouble("speed");
                gustTotal += weatherInfo.getJSONObject("wind").getDouble("gust");
                count++;
            } else {
                int aveTemp = (int) Math.round(((tempTotal / count) - 273.15) * 9 / 5 + 32);
                int aveWind = (int) Math.round((windTotal / count) * 2.23694);
                int aveGust = (int) Math.round((gustTotal / count) * 2.23694);

                result.add(new int[]{aveTemp, aveWind, aveGust});

                currentDate = date;
                count = 1;
                windTotal = weatherInfo.getJSONObject("wind").getDouble("speed");
                tempTotal = weatherInfo.getJSONObject("main").getDouble("temp");
                gustTotal = weatherInfo.getJSONObject("wind").getDouble("gust");
                repeat++;
            }
        }

        return result;
    }
}
