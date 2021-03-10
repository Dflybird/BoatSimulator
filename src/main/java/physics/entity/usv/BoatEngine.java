package physics.entity.usv;

import org.ode4j.math.DVector3;
import org.ode4j.ode.DBody;

/**
 * @Author Gq
 * @Date 2021/3/10 13:27
 * @Version 1.0
 **/
public class BoatEngine {



    /* 引擎驱动力，影响行驶速度，单位N */
    private float currentEnginePower;
    /* 转舵角度，影响行驶方向，单位度 */
    private float currentEngineRotation;

    private final DBody body;
    private final DVector3 engineRelativeCoordinate;

    public BoatEngine(DBody body, DVector3 engineRelativeCoordinate) {
        this.body = body;
        this.engineRelativeCoordinate = engineRelativeCoordinate;
    }

    public void setEnginePower(float power) {

    }

    public void setEngineRotation(float rotation) {

    }
}
