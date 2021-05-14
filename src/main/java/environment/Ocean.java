package environment;

import conf.SceneConfig;
import gui.Scene;
import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.obj.GameObj;
import gui.obj.OceanObj;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gq
 * @Date 2021/2/23 11:03
 * @Version 1.0
 **/
public class Ocean {

    private static final Logger logger = LoggerFactory.getLogger(Ocean.class);

    /** 菲利普斯常数,影响波高 */
    private static final float A = 0.0001f;
    /** N和M是在x轴与z轴上取样点数 */
    private static final int N = 64;
    private static final int M = 64;

    /* 单块海面x轴上长度 */
    private float lX;
    /* 单块海面z轴上长度 */
    private float lZ;
    /* x轴上单块海面数量 */
    private int nX;
    /* z轴上单块海面数量 */
    private int nZ;
    /* 海面中心点的坐标 y是平静时海面高度 */
    private Vector3f pos;

    private Wave wave;
    private Fog fog;
    private Wind wind;

    public Ocean(float lX, float lZ, int nX, int nZ, Vector3f pos) {
        this.lX = lX;
        this.lZ = lZ;
        this.nX = nX;
        this.nZ = nZ;
        this.pos = pos;
    }

    public void init(Scene scene, SceneConfig sceneConfig) {
        if (sceneConfig == null) {
            fog = Fog.OCEAN_FLOG;
            wind = new Wind(2, new Vector2f(-1,0));
        } else {
            fog = new Fog(true, new Vector3f(0.7f, 0.7f, 0.7f), 2f, sceneConfig.getFogVisibility());
            wind = sceneConfig.getWind();
        }
        wave = new Wave(lX, lZ, N, M, wind, A);
//        Material material = new Material(
//                new Vector4f(0.0f, 0.65f, 0.75f, 1.0f),
//                new Vector4f(0.5f, 0.65f, 0.75f, 1.0f),
//                new Vector4f(0.9f, 0.4f, 0.2f,  1.0f),
//                1, null);
        Material material = new Material(
                new Vector4f((float) 0x90/0xff, (float) 0xca/0xff, (float) 0xf9/0xff, 1.0f),
                new Vector4f((float) 0x90/0xff, (float) 0xca/0xff, (float) 0xf9/0xff, 1.0f),
                new Vector4f(1.0f, 0.7f, 0.5f,  1.0f),
                0.5f, null);
        Mesh mesh = new Mesh(wave.getModel(), material);
        //海洋平铺，先延Z轴平铺，再延X轴平铺
        List<GameObj> oceanBlocks = new ArrayList<>();
        float px, py, pz;
        py = pos.y;
        for (int i = 0; i < nX; i++) {
            px = pos.x + (i - (float)(nX-1)/2) * lX;
            for (int j = 0; j < nZ; j++) {
                pz = pos.z + (j - (float)(nZ-1)/2) * lZ;
                OceanObj obj = new OceanObj(new Vector3f(px, py, pz), new Quaternionf(), new Vector3f(1,1,1));
                obj.setMesh(mesh);
                oceanBlocks.add(obj);
            }
        }
        scene.setOceanBlock(oceanBlocks);
        //雾
        scene.setFog(fog);
    }

    public void update(double timeFromStart) {
        wave.evaluateWavesFFT((float) timeFromStart);
    }

    /**
     * 获得点(x,y1,z)到海浪(x,y2,z)处的距离
     * @param pos
     * @return
     */
    public float distanceToWave(float[] pos) {
        float h = getWaveHeight(pos[0], pos[2]);
        return pos[1] - h;
    }

    public float distanceToWave(Vector3f pos) {
        float h = getWaveHeight(pos.x, pos.z);
        return pos.y - h;
    }

    /**
     * 获得海面上(x,z)处海浪的高度y
     * @param x
     * @param z
     * @return
     */
    public float getWaveHeight(float x, float z) {
        //排除超过边界的情况
        float lengthX = lX * nX;
        float lengthZ = lZ * nZ;
        float initX = pos.x - lengthX/2;
        float initZ = pos.z - lengthZ/2;
        if (x - initX > lengthX || x - initX < 0) {
            return pos.y;
        }
        if (z - initZ > lengthZ || z -initZ < 0) {
            return pos.y;
        }

        //在一块海面中相对坐标
        float localX = (x - initX) % lX - lX / 2;
        float localZ = (z - initZ) % lZ - lZ / 2;

        //在一块海面中确定海浪对应的网格方块
        int index;
        float[] originalVertices = wave.getVertices();
        float[] vertices = wave.getVertices();

        //TODO 目前基于简化方式实现，有一定误差，假定在平面上坐标偏移不多
        // 用海浪原始坐标确定目标点位置，用现在点的高度作为原始点海浪高度
        // 需要解决对海浪高度采样的问题后才能进行改进

        //水面三角片三个顶点
        Vector3f pointA, pointB, pointC;
        //先确定x轴上位置
        float originX = originalVertices[0];
        float originZ = originalVertices[2];
        //每个网格长宽
        float meshX = lX/N;
        float meshZ = lZ/M;
        int n = (int) ((localX - originX)/meshX);
        int m = (int) ((localZ - originZ)/meshZ);
        index = m * (N + 1) + n;
        //index+NPlus1 -- index+NPlus1+1
        //    |          /      |
        //    |        /        |
        //    |      /          |
        //    |    /            |
        //  index ----------- index+1

        if ((localX-originX - n * meshX) > (localZ-originZ - m * meshZ)) {

            //右下角三角形
            pointA = new Vector3f(originalVertices[index * 3],
                    vertices[index * 3 + 1],
                    originalVertices[index * 3 + 2]);
            pointB = new Vector3f(originalVertices[(index + N + 2) * 3],
                    vertices[(index + N + 2) * 3 + 1],
                    originalVertices[(index + N + 2) * 3 + 2]);
            pointC = new Vector3f(originalVertices[(index + 1) * 3],
                    vertices[(index + 1) * 3 + 1],
                    originalVertices[(index + 1) * 3 + 2]);
        } else {
            //左上角三角形
            pointA = new Vector3f(originalVertices[index * 3],
                    vertices[index * 3 + 1],
                    originalVertices[index * 3 + 2]);
            pointB = new Vector3f(originalVertices[(index + N + 1) * 3],
                    vertices[(index + N + 1) * 3 + 1],
                    originalVertices[(index + N + 1) * 3 + 2]);
            pointC = new Vector3f(originalVertices[(index + N + 2) * 3],
                    vertices[(index + N + 2) * 3 + 1],
                    originalVertices[(index + N + 2) * 3 + 2]);
        }

        Vector3f AB = new Vector3f();
        Vector3f AC = new Vector3f();
        pointB.sub(pointA, AB);
        pointC.sub(pointA, AC);
        Vector3f abc = new Vector3f();
        AB.cross(AC, abc);
        float d = abc.dot(pointA);
        //三角形平面方程：ax+by+cz+d=0;
        return (d - abc.x * localX - abc.z * localZ) / abc.y;
    }

    public Wave getWave() {
        return wave;
    }

    public Fog getFog() {
        return fog;
    }

    public Wind getWind() {
        return wind;
    }
}
