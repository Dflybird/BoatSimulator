package physics.entity.geom;

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
        mass.setBox(density, 1,1,1);
        body = OdeHelper.createBody(world);
        body.setPosition(translation[0], translation[1], translation[2]);
        DGeom geom = OdeHelper.createBox(space, 1,1,1);
        geom.setBody(body);
    }

    @Override
    public float[] getTranslation() {
        if (body == null) {
            return translation;
        }
        DVector3C position = body.getPosition();
        translation[0] = (float) position.get0();
        translation[1] = (float) position.get1();
        translation[2] = (float) position.get2();
        return translation;
    }

    @Override
    public float[] getRotation() {
        return super.getRotation();
    }
}
