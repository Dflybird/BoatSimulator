package physics.entity.usv;

import environment.Ocean;
import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

import static util.StructTransform.transformFromVector3f;

/**
 * @Author Gq
 * @Date 2021/3/3 14:59
 * @Version 1.0
 **/
public class BoatEntity extends Entity {
    private final Logger logger = LoggerFactory.getLogger(BoatEntity.class);

    private Ocean ocean;
    private BoatEngine engine;

    //船头方向就是船的朝向，指向x轴正方向
    public BoatEntity(Ocean ocean, DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        super(world, space, translation, new Vector3f(1,0,0) , rotation, scale, model);
        this.ocean = ocean;
        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        //重量，单位kg
        float weight = 3000f * scale.x * scale.y * scale.z;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        meshData.build(model.getVertices(), model.getIndices());

        geom = OdeHelper.createTriMesh(space, meshData, null, null, null);

        mass.setTrimeshTotal(weight, (DTriMesh) geom);

        body = OdeHelper.createBody(world);
        body.setPosition(transformFromVector3f(translation));
        body.setMass(mass);
        geom.setBody(body);

        engine = new BoatEngine(ocean, body, new Vector3f(-2f, -1f, 0f));
    }

    @Override
    public void updateState() {
        super.updateState();

        engine.updateEngine(translation, forward, rotation);
//        body.addForceAtPos(new DVector3(1000,0,0),body.getPosition());
    }

    public BoatEngine getEngine() {
        return engine;
    }
}
