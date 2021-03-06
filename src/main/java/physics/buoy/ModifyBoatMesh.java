package physics.buoy;

import environment.Ocean;
import gui.obj.Model;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Gq
 * @Date 2021/3/4 19:15
 * @Version 1.0
 **/
public class ModifyBoatMesh {

    private static final Logger logger = LoggerFactory.getLogger(ModifyBoatMesh.class);

    private Ocean ocean;
    private Transform transform;
    private Entity entity;

    private float[] boatVertices;
    private int[] boatIndices;

    private Vector3f[] boatVerticesGlobal;
    private float[] allDistancesToSurface;

    private List<TriangleData> underSurfaceTriangleData = new ArrayList<>();

    public ModifyBoatMesh(Entity entity, Ocean ocean) {
        this.ocean = ocean;
        this.entity = entity;
        transform = new Transform(entity.getTranslation(), entity.getRotation(), entity.getScale());
        boatVertices = entity.getModel().getVertices();
        boatIndices = entity.getModel().getIndices();

        boatVerticesGlobal = new Vector3f[boatVertices.length/3];
        allDistancesToSurface = new float[boatVertices.length/3];
    }

    public void generateUnderwaterMesh(){
        underSurfaceTriangleData.clear();
        for (int i = 0; i < boatVertices.length / 3; i++) {
            //将模型坐标系的点转换到世界坐标系
            Vector3f globalPos = transform.transformPoint(boatVertices[i*3], boatVertices[i*3+1], boatVertices[i*3+2]);
            boatVerticesGlobal[i] = globalPos;

            allDistancesToSurface[i] = ocean.distanceToWave(globalPos);
        }
        //收集在水下的三角形
        addTriangles();
    }

    public Model getUnderwaterModel() {
        int faceNum = underSurfaceTriangleData.size();
        int[] indices = new int[faceNum * 3];

        //数据有大量重复
        float[] vertices = new float[faceNum * 3 * 3];
        float[] normals = new float[faceNum * 3 * 3];
        float[] textures = new float[faceNum * 3 * 2];

        int count = 0;
        for (int i = 0; i < faceNum; i++) {
            TriangleData triangleData = underSurfaceTriangleData.get(i);
            //将三角形顶点从世界坐标系转换到物体坐标系
            Vector3f p1 = transform.inverseTransformPoint(triangleData.getP1());
            Vector3f p2 = transform.inverseTransformPoint(triangleData.getP2());
            Vector3f p3 = transform.inverseTransformPoint(triangleData.getP3());
            Vector3f normal = triangleData.getNormal();

            vertices[count * 3] = p1.x;
            vertices[count * 3 + 1] = p1.y;
            vertices[count * 3 + 2] = p1.z;
            normals[count * 3] = normal.x;
            normals[count * 3 + 1] = normal.y;
            normals[count * 3 + 2] = normal.z;
            indices[count] = count;
            count++;

            vertices[count * 3] = p2.x;
            vertices[count * 3 + 1] = p2.y;
            vertices[count * 3 + 2] = p2.z;
            normals[count * 3] = normal.x;
            normals[count * 3 + 1] = normal.y;
            normals[count * 3 + 2] = normal.z;
            indices[count] = count;
            count++;

            vertices[count * 3] = p3.x;
            vertices[count * 3 + 1] = p3.y;
            vertices[count * 3 + 2] = p3.z;
            normals[count * 3] = normal.x;
            normals[count * 3 + 1] = normal.y;
            normals[count * 3 + 2] = normal.z;
            indices[count] = count;
            count++;
        }

        return new Model(indices, vertices, textures, normals);
    }

    private void addTriangles(){
        VertexData[] vertexData = new VertexData[3];
        vertexData[0] = new VertexData();
        vertexData[1] = new VertexData();
        vertexData[2] = new VertexData();

        int count = 0;
        while (count < boatIndices.length) {
            for (int i = 0; i < 3; i++) {
                vertexData[i].distance = allDistancesToSurface[boatIndices[count]];
                vertexData[i].index = i;
                vertexData[i].globalVertexPos = boatVerticesGlobal[boatIndices[count]];
                count++;
            }

            //三角形三个顶点都在水面上
            if (vertexData[0].distance > 0f && vertexData[1].distance > 0f && vertexData[2].distance > 0f) {
                continue;
            }

            //三角形三个顶点都在水面下
            if (vertexData[0].distance < 0f && vertexData[1].distance < 0f && vertexData[2].distance < 0f) {
                Vector3f p1 = vertexData[0].globalVertexPos;
                Vector3f p2 = vertexData[1].globalVertexPos;
                Vector3f p3 = vertexData[2].globalVertexPos;

                //Save the triangle
                underSurfaceTriangleData.add(new TriangleData(p1, p2, p3, ocean));
                continue;
            }

            //按照顶点离水面的距离从大到小排序，也就是顶点从高到低排序
            Arrays.sort(vertexData, (o1, o2) -> Float.compare(o2.distance, o1.distance));

            //一个顶点在水面上，两个顶点在水面下
            if (vertexData[0].distance > 0f && vertexData[1].distance < 0f && vertexData[2].distance < 0f) {
                addTrianglesOneAboveSurface(vertexData);
                continue;
            }

            //两个顶点在水面上，一个顶点在水面下
            if (vertexData[0].distance > 0f && vertexData[1].distance > 0f && vertexData[2].distance < 0f) {
                addTrianglesTwoAboveSurface(vertexData);
            }
        }
    }

    private void addTrianglesOneAboveSurface(VertexData[] vertexData){
        //vertexDate 已经从大到小排序，水面上的点是H，顺时针顶点顺序是H，L，M
        Vector3f H = vertexData[0].globalVertexPos;

        int indexM = vertexData[0].index - 1;
        if (indexM < 0) {
            indexM = 2;
        }

        //三角形三个顶点到水面的距离
        float h_H = vertexData[0].distance;
        float h_M;
        float h_L;

        Vector3f M;
        Vector3f L;

        if (vertexData[1].index == indexM) {
            M = vertexData[1].globalVertexPos;
            L = vertexData[2].globalVertexPos;

            h_M = vertexData[1].distance;
            h_L = vertexData[2].distance;
        } else {
            M = vertexData[2].globalVertexPos;
            L = vertexData[1].globalVertexPos;

            h_M = vertexData[2].distance;
            h_L = vertexData[1].distance;
        }

        //切掉水面上的三角形，保留水下部分形成两个新的三角形
        //计算点I_M
        Vector3f MH = new Vector3f();
        H.sub(M, MH);
        float t_M = -h_M / (h_H - h_M);
        Vector3f MI_M = MH.mul(t_M);
        Vector3f I_M = MI_M.add(M);

        //计算点I_L
        Vector3f LH = new Vector3f();
        H.sub(L, LH);
        float t_L = -h_L / (h_H - h_L);
        Vector3f LI_L = LH.mul(t_L);
        Vector3f I_L = LI_L.add(L);

        underSurfaceTriangleData.add(new TriangleData(M, I_M, I_L, ocean));
        underSurfaceTriangleData.add(new TriangleData(M, I_L, L, ocean));
    }

    private void addTrianglesTwoAboveSurface(VertexData[] vertexData){
        //vertexDate 已经从大到小排序，水面下的点是L，顺时针顶点顺序是H，M，L
        Vector3f L = vertexData[2].globalVertexPos;

        int indexH = vertexData[2].index + 1;
        if (indexH >2) {
            indexH = 0;
        }

        float h_L = vertexData[2].distance;
        float h_H;
        float h_M;

        Vector3f H;
        Vector3f M;

        if (vertexData[1].index == indexH) {
            H = vertexData[1].globalVertexPos;
            M = vertexData[0].globalVertexPos;

            h_H = vertexData[1].distance;
            h_M = vertexData[0].distance;
        } else {
            H = vertexData[0].globalVertexPos;
            M = vertexData[1].globalVertexPos;

            h_H = vertexData[0].distance;
            h_M = vertexData[1].distance;
        }

        //切掉并保留水面下的三角形
        //计算点J_M
        Vector3f LM = new Vector3f();
        M.sub(L, LM);
        float t_M = -h_L / (h_M - h_L);
        Vector3f LJ_M = LM.mul(t_M);
        Vector3f J_M = LJ_M.add(L);

        //计算点J_H
        Vector3f LH = new Vector3f();
        H.sub(L, LH);
        float t_H = -h_L / (h_H - h_L);
        Vector3f LJ_H = LH.mul(t_H);
        Vector3f J_H = LJ_H.add(L);

        underSurfaceTriangleData.add(new TriangleData(L, J_H, J_M, ocean));
    }

    public Entity getEntity() {
        return entity;
    }

    public List<TriangleData> getUnderSurfaceTriangleData() {
        return underSurfaceTriangleData;
    }

    public Transform getTransform() {
        return transform;
    }

    public static class Transform{
        private final Vector3f translation;
        private final Quaternionf rotation;
        private final Vector3f scale;
        private final Matrix4f matrix;
        private final Matrix4f matrixInv;
        private final Vector4f point;


        public Transform(Vector3f translation, Quaternionf rotation, Vector3f scale) {
            this.translation = translation;
            this.rotation = rotation;
            this.scale = scale;
            this.matrix = new Matrix4f();
            this.matrixInv = new Matrix4f();
            this.point = new Vector4f();
        }

        public Vector3f transformPoint(float x, float y, float z) {
            point.x = x;
            point.y = y;
            point.z = z;
            point.w = 1;
            matrix.identity()
                    .translate(translation)
                    .rotate(rotation)
                    .scale(scale);
            point.mul(matrix);
            return new Vector3f(point.x, point.y, point.z);
        }

        public Vector3f inverseTransformPoint(Vector3f p) {
            point.x = p.x;
            point.y = p.y;
            point.z = p.z;
            point.w = 1;
            matrix.invert(matrixInv);
            point.mul(matrixInv);
            return new Vector3f(point.x, point.y, point.z);
        }
    }

    private static class VertexData {
        //当前点到水面的距离
        private float distance;
        private int index;
        private Vector3f globalVertexPos;

    }
}
