import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Scanner {
    private static final Scanner SCANNER = new Scanner();
    private MainConfig mainConfig;
    private HttpClient client;
    private CloseableHttpClient closeableHttpClient;
    private HttpRequest httpRequest;
    private static final Logger LOGGER = Logger.getLogger(Scanner.class.getName());
    private ScheduledExecutorService scheduler;
    private static final String DATE_SP = "-";
    private static final String TIME_SP = ":";

    public static Scanner getInstance() {
        SCANNER.mainConfig = MainConfig.getInstance();
        return SCANNER;
    }

    public void init() {
        if (!mainConfig.isLoaded())
            System.exit(-1);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        client = HttpClient.newHttpClient();
        closeableHttpClient = HttpClients.createDefault();
        httpRequest = HttpRequest.newBuilder().uri(URI.create(mainConfig.getCamEstablishUri())).build();
        new Thread(() ->
                client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofFile(Path.of(mainConfig.getNullPath()))))
                .start();
        scheduler.scheduleAtFixedRate(this::checkCamera, mainConfig.getDelay(), mainConfig.getPeriod(), TimeUnit.SECONDS);
    }

    public void checkCamera() {
        httpRequest = HttpRequest.newBuilder().uri(URI.create(mainConfig.getCamTakeUri())).build();
        LocalDateTime time = LocalDateTime.now();
        String path = new StringBuilder(mainConfig.getPref())
                .append(time.getYear()).append(DATE_SP)
                .append(time.getMonth()).append(DATE_SP)
                .append(time.getDayOfMonth()).append(DATE_SP)
                .append(time.getHour()).append(TIME_SP)
                .append(time.getMinute()).append(TIME_SP)
                .append(time.getSecond())
                .append(mainConfig.getSuf())
                .toString();
        File imgFile = new File(path);
        try {
            client.send(httpRequest, HttpResponse.BodyHandlers.ofFile(Path.of(imgFile.getAbsolutePath())));
            LOGGER.info("Taken");
            if (!isOK(path)) {
                LOGGER.warning("Not appropriate");
                sendFile(imgFile);
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Taking picture failed");
        }
    }

    private void sendFile(File file) {
        URI startUploadUri = URI.create(mainConfig.getStartUploadUri() + file.getName());
        URI fileUploadUri = URI.create(mainConfig.getFileUploadUri());
        httpRequest = HttpRequest.newBuilder().uri(startUploadUri).GET().build();
        try {
            client.send(httpRequest, HttpResponse.BodyHandlers.discarding());
            LOGGER.info("Starting upload...");
            HttpEntity entity = MultipartEntityBuilder.create().addBinaryBody(mainConfig.getFileKey(), file).build();
            HttpPost httpPost = new HttpPost(fileUploadUri);
            httpPost.setEntity(entity);
            try {
                closeableHttpClient.execute(httpPost);
                LOGGER.info("Upload successful");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to upload the file");
            }
        } catch (IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Starting upload failed");
        }
    }

    private boolean isOK(String file) {
        Optional<List<String>> results = PythonUtil.runPython(mainConfig.getPyFile(), file);
        if (results.isPresent()) {
            return Boolean.parseBoolean(results.get().get(0));
        } else {
            LOGGER.warning("process failed");
            return true;
        }
    }
}
