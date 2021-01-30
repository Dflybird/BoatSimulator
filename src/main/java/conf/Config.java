package conf;

/**
 * @Author Gq
 * @Date 2021/1/28 19:52
 * @Version 1.0
 **/
public class Config {

    private static Config instance;

    public static Config loadConfig() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private boolean visible = true;

    public boolean isVisible() {
        return visible;
    }
}
