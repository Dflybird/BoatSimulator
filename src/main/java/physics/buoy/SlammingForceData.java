package physics.buoy;

import org.joml.Vector3f;

/**
 * @Author Gq
 * @Date 2021/3/6 21:49
 * @Version 1.0
 **/
public class SlammingForceData {
    //初始三角形面积
    private float originalArea;
    //水面下三角形面积
    private float submergeArea;
    //上个更新时刻水下三角形面积
    private float previousSubmergeArea;
    //三角形中心坐标，世界坐标系
    private Vector3f triangleCenter = new Vector3f();
    //三角形运动速度
    private Vector3f velocity = new Vector3f();
    //上个更新时刻三角形运动速度
    private Vector3f previousVelocity = new Vector3f();

    public float getOriginalArea() {
        return originalArea;
    }

    public float getSubmergeArea() {
        return submergeArea;
    }

    public float getPreviousSubmergeArea() {
        return previousSubmergeArea;
    }

    public Vector3f getTriangleCenter() {
        return triangleCenter;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getPreviousVelocity() {
        return previousVelocity;
    }

    public void setOriginalArea(float originalArea) {
        this.originalArea = originalArea;
    }

    public void setSubmergeArea(float submergeArea) {
        this.submergeArea = submergeArea;
    }

    public void setPreviousSubmergeArea(float previousSubmergeArea) {
        this.previousSubmergeArea = previousSubmergeArea;
    }

    public void setTriangleCenter(Vector3f triangleCenter) {
        this.triangleCenter.set(triangleCenter);
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity.set(velocity);
    }

    public void setPreviousVelocity(Vector3f previousVelocity) {
        this.previousVelocity.set(previousVelocity);
    }
}
