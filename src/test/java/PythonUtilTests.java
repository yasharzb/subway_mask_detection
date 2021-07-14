import org.junit.jupiter.api.Test;

public class PythonUtilTests {
    @Test
    public void runPython_simple_Test() {
        MainConfig mainConfig = MainConfig.getInstance();
        PythonUtil.runPython(mainConfig.getPyFile());
    }
}
