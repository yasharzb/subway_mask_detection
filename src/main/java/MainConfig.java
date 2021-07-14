import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MainConfig {
    private static final MainConfig MAIN_CONFIG = new MainConfig();
    private String camTakeUri;
    private String camEstablishUri;
    private String nullPath;
    private String pref;
    private String suf;
    private String startUploadUri;
    private String fileUploadUri;
    private String fileKey;
    private String pyFile;
    private int period;
    private int delay;
    private boolean isLoaded;
    private static final Logger LOGGER = Logger.getLogger(MainConfig.class.getName());

    public static MainConfig getInstance() {
        MAIN_CONFIG.isLoaded = false;
        loadProperties();
        return MAIN_CONFIG;
    }

    private static void loadProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("config.properties"));
            MAIN_CONFIG.period = Integer.parseInt(properties.getProperty("period"));
            MAIN_CONFIG.delay = Integer.parseInt(properties.getProperty("delay"));
            MAIN_CONFIG.camTakeUri = properties.getProperty("cam-take-uri");
            MAIN_CONFIG.camEstablishUri = properties.getProperty("cam-establish-uri");
            MAIN_CONFIG.nullPath = properties.getProperty("null-path");
            MAIN_CONFIG.pref = properties.getProperty("pref");
            MAIN_CONFIG.suf = properties.getProperty("suf");
            MAIN_CONFIG.isLoaded = true;
            MAIN_CONFIG.startUploadUri = properties.getProperty("start-upload-uri");
            MAIN_CONFIG.fileUploadUri = properties.getProperty("file-upload-uri");
            MAIN_CONFIG.fileKey = properties.getProperty("file-key");
            MAIN_CONFIG.pyFile = properties.getProperty("py-file");
        } catch (IOException e) {
            LOGGER.warning("Failed to load config file");
        }
    }
}
