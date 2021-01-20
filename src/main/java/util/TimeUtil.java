package util;

/**
 * @Author: gq
 * @Date: 2021/1/20 17:34
 */
public class TimeUtil {

    /**
     *
     * @return 时间，单位秒
     */
    public static double currentTime() {
        return System.nanoTime() / 1_000_000_000.0;
    }
}
