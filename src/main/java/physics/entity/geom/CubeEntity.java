package physics.entity.geom;

import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

/**
 * @Author Gq
 * @Date 2021/2/22 18:30
 * @Version 1.0
 **/
public class CubeEntity extends Entity {
    private final Logger logger = LoggerFactory.getLogger(CubeEntity.class);

    private DWorld world;
    private DSpace space;

    private DBody body;
    private DGeom geom;

    public CubeEntity(float[] translation, float[] rotation, float[] scale) {
        super(translation, rotation, scale);
    }

    public CubeEntity(DWorld world, DSpace space, float[] translation, float[] rotation, float[] scale) {
        super(translation, rotation, scale);
        this.world = world;
        this.space = space;

        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        float density = 1f;
        //立方体大小
        mass.setBox(density, scale[0],scale[1],scale[2]);
        body = OdeHelper.createBody(world);
        body.setPosition(translation[0], translation[1], translation[2]);
        body.setMass(mass);
        geom = OdeHelper.createBox(space,  scale[0],scale[1],scale[2]);
        geom.setBody(body);
    }

    @Override
    public float[] getTranslation() {
        if (body == null) {
            return translation;
        }
        DVector3C position = geom.getPosition();
        translation[0] = (float) position.get0();
        translation[1] = (float) position.get1();
        translation[2] = (float) position.get2();
        return translation;
    }

    @Override
    public float[] getRotation() {
        DMatrix3C currentRotation = geom.getRotation();
        float[] curRotation = new float[3];
        //TODO 修改精度损失问题
        curRotation[0] = (float) (rotation[0] * currentRotation.get00() + rotation[1] * currentRotation.get01() + rotation[2] * currentRotation.get02());
        curRotation[1] = (float) (rotation[0] * currentRotation.get10() + rotation[1] * currentRotation.get11() + rotation[2] * currentRotation.get12());
        curRotation[2] = (float) (rotation[0] * currentRotation.get20() + rotation[1] * currentRotation.get21() + rotation[2] * currentRotation.get22());
//        logger.debug("rotation x: {}|y: {} |z: {}", curRotation[0], translation.y, translation.z);
        return curRotation;
    }
}
