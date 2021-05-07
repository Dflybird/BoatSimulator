package data_acquisition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conf.Constant;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: gq
 * @Date: 2021/5/7 15:16
 */
public class SpeedData {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<Float> speed = new LinkedList<>();

    public void writeToFile() {
        try {
            File file = new File(Constant.DEFAULT_RESOURCES_DIR + "/data", "sim_speed_data.json");
            FileUtil.writeFile(file, gson.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
