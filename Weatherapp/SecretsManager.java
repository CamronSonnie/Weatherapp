import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SecretsManager {
    private static final String SECRETS_FILE = "secrets.properties";
    private static Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream(SECRETS_FILE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getApiKey() {
        return properties.getProperty("api.key");
    }
}
