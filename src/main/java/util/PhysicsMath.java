package util;

import org.joml.Vector3f;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.buoy.SlammingForceData;
import physics.buoy.TriangleData;

import static conf.Constant.*;
import static util.StructTransform.transformToVector3f;

/**
 * @Author Gq
 * @Date 2021/3/6 17:33
 * @Version 1.0
 **/
public class PhysicsMath {

    private final static Logger logger = LoggerFactory.getLogger(PhysicsMath.class);

    //计算三角片中心的移动速度
    public static Vector3f triangleVelocity(DGeom geom, Vector3f triangleCenter) {
        // v_A = v_B + omega_B cross r_BA
        // v_A - velocity in point A
        // v_B - velocity in point B
        // omega_B - angular velocity in point B
        // r_BA - vector between A and B
        Vector3f v_B = transformToVector3f(geom.getBody().getLinearVel());
        Vector3f omega_B = transformToVector3f(geom.getBody().getAngularVel());
        Vector3f r_BA = new Vector3f();
        triangleCenter.sub(transformToVector3f(geom.getPosition()), r_BA);
        return v_B.add(omega_B.cross(r_BA));
    }

    //通过三角形三个顶点坐标计算三角形面积
    public static float triangleArea(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f t1 = new Vector3f();
        Vector3f t2 = new Vector3f();
        p2.sub(p1, t1);
        p3.sub(p1, t2);

        float a = p1.distance(p2);
        float c = p3.distance(p1);

        return (a * c * (float) Math.sin(t1.angle(t2))) / 2f;
    }

    //通过三角形三个顶点坐标计算三角形面积
    public static Vector3f triangleCenter(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f center = new Vector3f();
        center.add(p1).add(p2).add(p3).div(3);

        return center;
    }

    //计算浮力
    public static Vector3f buoyancyForce(float rho, TriangleData data) {
        // F_buoyancy = rho * g * V
        // rho - density of the mediaum you are in
        // g - gravity
        // V - volume of fluid directly above the curved surface
        // V = z * S * n
        // z - distance to surface
        // S - surface area
        // n - normal to the surface
        Vector3f buoyancyForce  = new Vector3f(data.getNormal());
        buoyancyForce .mul(-g * rho * data.getDistanceToSurface() * data.getArea());
        buoyancyForce.x = 0;
        buoyancyForce.z = 0;
        return checkForceIsValid(buoyancyForce, "Buoyancy");
    }

    //计算摩擦阻力系数
    public static float resistanceCoefficient(float velocity, float length) {
        // Rn = (V * L) / nu
        // V - 物体运动速度
        // L - 流体穿过表面的长度
        // nu - 流体粘性 [m^2 / s]

        //20摄氏度时流体粘性为 0.000001f
        //30摄氏度时流体粘性为 0.0000008f
        float nu = 0.000001f;

        float Rn = (velocity * length) / nu;
        return 0.075f / (float) (Math.pow((Math.log10(Rn) - 2), 2));
    }

    //水面粘性阻力
    public static Vector3f viscousWaterResistanceForce(float rho, TriangleData data, float Cf) {
        // F = 0.5 * rho * v^2 * S * Cf
        // rho - density of the medium you have
        // v - speed
        // S - surface area
        // Cf - 摩擦阻力系数
        Vector3f B = new Vector3f(data.getNormal());
        Vector3f A = new Vector3f(data.getVelocity());

//        logger.debug("t_v {}", A.length());
        float magnitudeB = B.length();
        Vector3f velocityTangent = new Vector3f();
        Vector3f temp = new Vector3f();

        A.cross(B, temp);
        temp.div(magnitudeB);
        B.cross(temp, velocityTangent);
        velocityTangent.div(magnitudeB);

        Vector3f tangentialDirection = new Vector3f(velocityTangent);
        tangentialDirection.normalize().mul(-1f);

        //水流速度，垂直于三角形平面
        Vector3f fluidVelocity = new Vector3f(tangentialDirection);
        fluidVelocity.mul(A.length());

        Vector3f resistanceForce = new Vector3f(fluidVelocity);
        resistanceForce.mul(0.5f * rho * fluidVelocity.length() * data.getArea() * Cf);
        return checkForceIsValid(resistanceForce, "Viscous Water Resistance");
    }

    //计算阻力
    public static Vector3f pressureDragForce(TriangleData data) {
        //影响转弯
        //f_p和f_S -衰减功率，应该小于1

        float velocity = data.getVelocity().length();

        float velocityReference = VELOCITY_REFERENCE;
//        float velocityReference = velocity;

        velocity = velocity / velocityReference;

        Vector3f pressureDragForce = new Vector3f(data.getNormal());
        if (data.getCosTheta() > 0) {
            //三角面与运动方向相同，计算流体正向压力
            pressureDragForce.mul(-(C_PD1 * velocity + C_PD2 * (velocity * velocity)) * data.getArea() * (float) Math.pow(data.getCosTheta(), f_P));
        } else {
            //三角面与运动方向相反，计算流体逆向吸力
            pressureDragForce.mul((C_SD1 * velocity + C_SD2 * (velocity * velocity)) * data.getArea() * (float) Math.pow(Math.abs(data.getCosTheta()), f_S));
        }
//        logger.debug("p_f {} | {} | {}", pressureDragForce.x, pressureDragForce.y, pressureDragForce.z);
        return checkForceIsValid(pressureDragForce, "Pressure drag");
    }

    //计算入水撞击力
    public static Vector3f slammingForce(SlammingForceData slammingData, TriangleData triangleData, float boatArea, float boatMass, float stepTime) {
        //如何三角面的发现与速度方向相同，并且入水面积不为0，计算撞击力
        if (triangleData.getCosTheta() < 0f || slammingData.getOriginalArea() <= 0f) {
            return new Vector3f();
        }

        //计算加速度
        Vector3f dV = new Vector3f(slammingData.getVelocity());
        dV.mul(slammingData.getSubmergeArea());
        Vector3f dVPre = new Vector3f(slammingData.getPreviousVelocity());
        dVPre.mul(slammingData.getPreviousSubmergeArea());
        Vector3f accelerateV = new Vector3f();
        dV.sub(dVPre, accelerateV);
        accelerateV.div(slammingData.getOriginalArea() * stepTime);

        float acc = accelerateV.length();

        //计算入水撞击力
        // F = clamp(acc / acc_max, 0, 1)^p * cos(theta) * F_stop
        // p - power to ramp up slamming force - should be 2 or more

        // F_stop = m * v * (2A / S)
        // m - 物体质量
        // v - 速度
        // A - 三角片面积
        // S - 物体表面积
        Vector3f F_stop = new Vector3f(triangleData.getVelocity());
        F_stop.mul(boatMass * 2f * triangleData.getArea() / boatArea);

//        float p = P;
//        float acc_max = ACC_MAX;
        float p = 2f;
        float acc_max = acc;
        float slammingCheat = SLAMMING_CHEAT;

        Vector3f slammingForce = F_stop.mul(-1 * slammingCheat * triangleData.getCosTheta() * (float) Math.pow(clamp(acc/acc_max,0, 1), p));
        return checkForceIsValid(slammingForce, "Slamming");
    }

    public static float residualResistanceForce() {
        // R_r = R_pressure + R_wave = 0.5 * rho * v * v * S * C_r
        // rho - water density
        // v - speed of ship
        // S - surface area of the underwater portion of the hull
        // C_r - coefficient of residual resistance - increases as the displacement and speed increases

        //float residualResistanceForce = 0.5f * rho * v * v * S * C_r;
        return 0;
    }

    //空气阻力，通常占所有阻力的4%~8%
    public static Vector3f airResistanceForce(float rho, TriangleData triangleData, float C_air) {
        // R_air = 0.5 * rho * v^2 * A_p * C_air
        // rho - air density
        // v - speed of ship
        // A_p - projected transverse profile area of ship
        // C_r - coefficient of air resistance (drag coefficient)

        //只有三角片迎风的时候计算空气阻力
        if (triangleData.getCosTheta() < 0f) {
            return new Vector3f();
        }

        Vector3f airResistanceForce = new Vector3f(triangleData.getVelocity());
        airResistanceForce.mul(0.5f * rho * triangleData.getVelocity().length() * triangleData.getArea() * C_air);
        return checkForceIsValid(airResistanceForce, "Air Resistance");
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    private static Vector3f checkForceIsValid(Vector3f force, String forceName) {
        if (!Float.isNaN(force.x + force.y + force.z)) {
            return force;
        } else {
            logger.debug("{} force is NaN.", forceName);
            return new Vector3f();
        }
    }
}
