package gui.graphic.light;

import org.joml.Vector3f;

/**
 * @Author Gq
 * @Date 2021/1/31 13:42
 * @Version 1.0
 **/
public class SpotLight {

    private PointLight pointLight;
    /** 指向聚光灯朝向方向 */
    private Vector3f coneDirection;
    /** 聚光灯锥的锥角,以cos(coneAngle)的方式存储 */
    private float cosAngle;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float coneAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        setConeAngle(coneAngle);
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCosAngle() {
        return cosAngle;
    }

    public void setCosAngle(float cosAngle) {
        this.cosAngle = cosAngle;
    }

    public void setConeAngle(float coneAngle) {
        this.setCosAngle((float) Math.cos(Math.toRadians(coneAngle)));
    }
}
