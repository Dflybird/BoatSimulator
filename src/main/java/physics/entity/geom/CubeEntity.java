package physics.entity.geom;

import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DQuaternionC;
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

    public CubeEntity(DWorld world, DSpace space, float[] translation, float[] rotation, float[] scale) {
        super(world, space, translation, rotation, scale);

        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        float density = 0.9f;
        //立方体大小
        mass.setBox(density, scale[0],scale[1],scale[2]);
        body = OdeHelper.createBody(world);
        body.setPosition(translation[0], translation[1], translation[2]);
        body.setMass(mass);
        geom = OdeHelper.createBox(space,  scale[0],scale[1],scale[2]);
        geom.setBody(body);
    }

}
