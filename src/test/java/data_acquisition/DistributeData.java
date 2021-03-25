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
 * @Date: 2021/3/24 19:02
 */
public class DistributeData {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final int nodeNum;

    public float elapsedTime;

    public float updateTime;

    public float msgNum;

    public DistributeData(int nodeNum) {
        this.nodeNum = nodeNum;
    }

    public void writeToFile() {
        try {
            File file = new File(Constant.DEFAULT_RESOURCES_DIR + "/data", String.format("dis_data_%d.json", nodeNum));
            FileUtil.writeFile(file, gson.toJson(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
