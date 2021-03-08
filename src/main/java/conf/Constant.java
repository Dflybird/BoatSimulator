package conf;

/**
 * @Author: gq
 * @Date: 2020/12/3 14:51
 */
public class Constant {

    public static final String DEFAULT_RESOURCES_DIR = "src/main/resources";

    public static final String RESOURCES_MODELS_DIR = DEFAULT_RESOURCES_DIR + "/models";

    public static final String RESOURCES_SHADERS_DIR = DEFAULT_RESOURCES_DIR + "/shaders";

    public static final String CONFIG_FILE_NAME = "config.json";

    //boat1 长5m 宽2m 高0.5m
    //boat2 长5m 宽2m 高1m
    public static final String BOAT_OBJ_NAME = "boat2.obj";

    public static final String BOAT_MTL_NAME = "boat2.mtl";

    /** 环境相关参数 */
    //重力系数 m/s^2
    public static final float g = 9.81f;
    //水密度 1000 kg/m^3 = 1 g/cm^3
    public static final float RHO_WATER = 1000f;
    //海水密度 kg/m^3
    public static final float RHO_OCEAN_WATER = 1027f;
    //空气密度 kg/m^3
    public static final float RHO_AIR = 1.225f;

    /** 浮力计算相关参数 */
    //参考运动速度
    public static final float VELOCITY_REFERENCE = 10f;
    //运动方向与流体正向压力系数
    public static final float C_PD1 = 10f;
    public static final float C_PD2 = 10f;
    public static final float f_P = 0.5f;
    //运动方向与流体逆向吸力系数
    public static final float C_SD1 = 10f;
    public static final float C_SD2 = 10f;
    public static final float f_S = 0.5f;
    //流体撞击力参数
    public static final float P = 2f;
    public static final float ACC_MAX = 2 * g;
    //取值范围0~1，应用撞击力大小系数，0忽略撞击力
    public static final float SLAMMING_CHEAT = 0.5f;
    //空气阻力系数，与物体材质和表面粗糙度有关，这里取0.4~0.6
    public static final float C_AIR = 0.4f;

}
