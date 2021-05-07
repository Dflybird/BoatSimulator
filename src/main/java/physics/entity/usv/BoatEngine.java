package physics.entity.usv;

import conf.Constant;
import environment.Ocean;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3;
import org.ode4j.ode.DBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;
import util.StructTransform;

import static conf.Constant.*;

/**
 * @Author Gq
 * @Date 2021/3/10 13:27
 * @Version 1.0
 **/
public class BoatEngine {

    private final Logger logger = LoggerFactory.getLogger(BoatEngine.class);

    /* 引擎驱动力，影响行驶速度，单位N */
    private float currentEnginePower;
    /* 转舵角度，影响行驶方向，单位度 */
    private float currentEngineRotation;

    private final Ocean ocean;
    private final Entity entity;
    private final Vector3f engineRelativeCoordinate;

    private final float maxPow;
    private final float maxAngle;
    private final float maxSpeed;

    public BoatEngine(Entity entity, Vector3f engineRelativeCoordinate, float maxPow, float maxAngle, float maxSpeed) {
        this.ocean = entity.getOcean();
        this.entity = entity;
        this.engineRelativeCoordinate = engineRelativeCoordinate;
        this.maxPow = maxPow;
        this.maxAngle = (float) Math.toRadians(maxAngle);
        this.maxSpeed = maxSpeed;
    }

    public void setEnginePower(float power) {
        if (entity.getBody().getLinearVel().length() <= maxSpeed && power <= maxPow) {
            currentEnginePower = power;
        }
    }

    /**
     * 设置船舵转向角度，正为右转，负为左转
     * @param angle
     */
    public void setEngineRotation(float angle) {
        if (angle > maxAngle) {
            currentEngineRotation = maxAngle;
            return;
        }
        if (angle < -maxAngle) {
            currentEngineRotation = -maxAngle;
            return;
        }
        currentEngineRotation = angle;
    }

    public void updateEngine(){
        Vector3f translation = entity.getTranslation();
        Vector3f forward = entity.getForward();
        Quaternionf rotation = entity.getRotation();

        Vector4f point = new Vector4f();
        Matrix4f matrix = new Matrix4f();
        point.x = engineRelativeCoordinate.x;
        point.y = engineRelativeCoordinate.y;
        point.z = engineRelativeCoordinate.z;
        point.w = 1;
        matrix.identity()
                .translate(translation)
                .rotate(rotation);
        point.mul(matrix);
        Vector3f enginePos = new Vector3f(point.x,point.y,point.z);

        float waterLevel = ocean.getWaveHeight(enginePos.x, enginePos.z);

        if (enginePos.y < waterLevel) {
            //引擎在水面下
            Vector3f force = new Vector3f(forward);
            force.rotateY(currentEngineRotation);
            force.rotate(rotation);
            force.normalize().mul(currentEnginePower);
            entity.getBody().addForceAtPos(StructTransform.transformFromVector3f(force),
                    StructTransform.transformFromVector3f(enginePos));
        } else {
            entity.getBody().addForceAtPos(StructTransform.transformFromVector3f(new Vector3f()),
                    StructTransform.transformFromVector3f(enginePos));
        }
    }

    public float getMaxPow() {
        return maxPow;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }
}
