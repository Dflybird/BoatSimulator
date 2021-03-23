package data_acquisition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conf.Constant;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @Author: gq
 * @Date: 2021/3/22 15:46
 */
public class USVBuoyData {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public List<Float> buoyData = new ArrayList<>();

    public void writeToFile() {
        try {
            File file = new File(Constant.DEFAULT_RESOURCES_DIR + "/data", "usv_buoy_data.json");
            FileUtil.writeFile(file, gson.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
