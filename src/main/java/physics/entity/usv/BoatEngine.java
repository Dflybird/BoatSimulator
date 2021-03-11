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
    private final DBody body;
    private final Vector3f engineRelativeCoordinate;

    public BoatEngine(Ocean ocean, DBody body, Vector3f engineRelativeCoordinate) {
        this.ocean = ocean;
        this.body = body;
        this.engineRelativeCoordinate = engineRelativeCoordinate;
    }

    public void setEnginePower(float power) {
        if (body.getLinearVel().length() <= MAX_SPEED && power <= MAX_POWER) {
            currentEnginePower = power;
        }
    }

    /**
     * 设置船舵转向角度，正为右转，负为左转
     * @param angle
     */
    public void setEngineRotation(float angle) {
        if (angle > MAX_ANGLE) {
            currentEngineRotation = MAX_ANGLE;
            return;
        }
        if (angle < -MAX_ANGLE) {
            currentEngineRotation = -MAX_ANGLE;
            return;
        }
        currentEngineRotation = angle;
    }

    public void updateEngine(Vector3f translation, Vector3f forward, Quaternionf rotation){
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

        if (translation.y < waterLevel) {
            //引擎在水面下
            Vector3f force = new Vector3f(forward);
            force.rotateY(currentEngineRotation);
            force.rotate(rotation);
            force.normalize().mul(currentEnginePower);
            body.addForceAtPos(StructTransform.transformFromVector3f(force),
                    StructTransform.transformFromVector3f(enginePos));
        } else {
            body.addForceAtPos(StructTransform.transformFromVector3f(new Vector3f()),
                    StructTransform.transformFromVector3f(enginePos));
        }
    }
}
