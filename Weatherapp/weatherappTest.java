import org.json.*;

public class weatherappTest {

    public static void main(String[] args) {
        testGetAveTemp();
        testGetAveWind();
        testGetAveGust();
    }

    static void testGetAveTemp() {
        JSONObject weatherInfo = new JSONObject();
        weatherInfo.put("main", new JSONObject().put("temp", 300)); // Temperature in Kelvin
        int actualTemp = weatherapp.getAveTemp(weatherInfo);
        int expectedTemp = 80; // Expected temperature in Fahrenheit
        if (actualTemp == expectedTemp) {
            System.out.println("testGetAveTemp: Passed");
        } else {
            System.out.println("testGetAveTemp: Failed. Expected: " + expectedTemp + ", Actual: " + actualTemp);
        }
    }

    static void testGetAveWind() {
        JSONObject weatherInfo = new JSONObject();
        weatherInfo.put("wind", new JSONObject().put("speed", 5)); // Wind speed in meter/second
        int actualWindSpeed = weatherapp.getAveWind(weatherInfo);
        int expectedWindSpeed = 11; // Expected wind speed in mph
        if (actualWindSpeed == expectedWindSpeed) {
            System.out.println("testGetAveWind: Passed");
        } else {
            System.out.println("testGetAveWind: Failed. Expected: " + expectedWindSpeed + ", Actual: " + actualWindSpeed);
        }
    }

    static void testGetAveGust() {
        JSONObject weatherInfo = new JSONObject();
        weatherInfo.put("wind", new JSONObject().put("gust", 10)); // Wind gust speed in meter/second
        int actualGustSpeed = weatherapp.getAveGust(weatherInfo);
        int expectedGustSpeed = 22; // Expected gust speed in mph
        if (actualGustSpeed == expectedGustSpeed) {
            System.out.println("testGetAveGust: Passed");
        } else {
            System.out.println("testGetAveGust: Failed. Expected: " + expectedGustSpeed + ", Actual: " + actualGustSpeed);
        }
    }
}
