package physics.buoy;

import environment.Ocean;
import org.joml.Vector3f;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import util.PhysicsMath;

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

    //三角形质心，世界坐标
    private final Vector3f center;

    private final float distanceToSurface;

    private final float area;

    //三角形中心的移动速度
    private final Vector3f velocity;
    //速度向量
    private final Vector3f velocityDir;
    //三角片法线与速度的夹角，同向为正，反向为负
    private final float cosTheta;

    public TriangleData(Vector3f p1, Vector3f p2, Vector3f p3, Ocean ocean, DGeom geom) {
        this.p1 = new Vector3f(p1);
        this.p2 = new Vector3f(p2);
        this.p3 = new Vector3f(p3);

        center = new Vector3f();
        center.add(p1).add(p2).add(p3).div(3);

        distanceToSurface = Math.abs(ocean.distanceToWave(center));

        Vector3f t1 = new Vector3f();
        Vector3f t2 = new Vector3f();
        p2.sub(p1, t1);
        p3.sub(p1, t2);
        normal = new Vector3f(t1);
        normal.cross(t2);
        normal.normalize();

        float a = p1.distance(p2);
        float c = p3.distance(p1);

        area = (a * c * (float) Math.sin(t1.angle(t2))) / 2f;

        velocity = PhysicsMath.triangleVelocity(geom, center);
        velocityDir = new Vector3f(velocity);
        velocityDir.normalize();

        cosTheta = velocityDir.dot(normal);
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

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getVelocityDir() {
        return velocityDir;
    }

    public float getCosTheta() {
        return cosTheta;
    }
}
