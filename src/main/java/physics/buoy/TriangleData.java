package physics.buoy;

import environment.Ocean;
import org.joml.Vector3f;

/**
 * @Author Gq
 * @Date 2021/3/4 18:51
 * @Version 1.0
 **/
public class TriangleData {
    //三角形三个点的世界坐标
    private final Vector3f p1;
    private final Vector3f p2;
    private final Vector3f p3;
    private final Vector3f normal;

    //三角形质心
    private final Vector3f center;

    private final float distanceToSurface;

    private final float area;

    public TriangleData(Vector3f p1, Vector3f p2, Vector3f p3, Ocean ocean) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;

        center = new Vector3f();
        center.add(p1).add(p2).add(p3).div(3);

        distanceToSurface = Math.abs(ocean.distanceToWave(center));

        Vector3f t1 = new Vector3f();
        Vector3f t2 = new Vector3f();
        normal = new Vector3f();
        p2.sub(p1, t1);
        p3.sub(p1, t2);
        t1.cross(t2, normal);
        normal.normalize();

        float a = p1.distance(p2);
        float c = p3.distance(p1);

        area = (a * c * (float) Math.sin(t1.angle(t2) * Math.PI / 180)) / 2f;
    }

    public Vector3f getP1() {
        return p1;
    }

    public Vector3f getP2() {
        return p2;
    }

    public Vector3f getP3() {
        return p3;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public Vector3f getCenter() {
        return center;
    }

    public float getDistanceToSurface() {
        return distanceToSurface;
    }

    public float getArea() {
        return area;
    }
}
