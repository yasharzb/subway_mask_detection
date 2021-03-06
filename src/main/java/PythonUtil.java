import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PythonUtil {
    public static Optional<List<String>> runPython(String pythonFile, String... args) {
        List<String> commandAndArgs = new ArrayList<>();
        commandAndArgs.add("python3");
        commandAndArgs.add(pythonFile);
        commandAndArgs.addAll(Arrays.asList(args));
        ProcessBuilder processBuilder = new ProcessBuilder(commandAndArgs);
        try {
            Process process = processBuilder.start();
            List<String> results = new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
                    .collect(Collectors.toList());
            int exitCode = process.waitFor();
            assert exitCode == 0;
            return Optional.of(results);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
