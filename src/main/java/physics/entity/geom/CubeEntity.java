package physics.entity.geom;

import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

import static util.StructTransform.*;
import static util.StructTransform.transformFromVector3f;

/**
 * @Author Gq
 * @Date 2021/2/22 18:30
 * @Version 1.0
 **/
public class CubeEntity extends Entity {
    private final Logger logger = LoggerFactory.getLogger(CubeEntity.class);

    public CubeEntity(DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        super(world, space, translation, rotation, scale, model);

        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        float density = 0.8f;
        //立方体大小
        mass.setBox(density, transformFromVector3f(scale));
        body = OdeHelper.createBody(world);
        body.setPosition(transformFromVector3f(translation));
        body.setMass(mass);
        geom = OdeHelper.createBox(space, transformFromVector3f(scale));
        geom.setBody(body);
    }



}
