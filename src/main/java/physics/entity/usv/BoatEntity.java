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
import physics.buoy.BuoyHelper;
import physics.entity.Entity;

import static util.StructTransform.transformFromQuaternionf;
import static util.StructTransform.transformFromVector3f;

/**
 * @Author Gq
 * @Date 2021/3/3 14:59
 * @Version 1.0
 **/
public class BoatEntity extends Entity {
    private final Logger logger = LoggerFactory.getLogger(BoatEntity.class);

    private BuoyHelper buoyHelper;

    //船头方向就是船的朝向，指向x轴正方向
    public BoatEntity(Ocean ocean, DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        super(ocean, world, space, translation, new Vector3f(1,0,0) , rotation, scale, model);
        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        //重量，单位kg
        float weight = 2000f * scale.x * scale.y * scale.z;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        meshData.build(model.getVertices(), model.getIndices());

        geom = OdeHelper.createTriMesh(space, meshData, null, null, null);

        mass.setTrimeshTotal(weight, (DTriMesh) geom);

        body = OdeHelper.createBody(world);
        body.setMass(mass);
        body.setPosition(transformFromVector3f(translation));
        body.setQuaternion(transformFromQuaternionf(rotation));
        geom.setBody(body);

    }

    @Override
    public void reset() {
        super.reset();
        destroy();
        init();
    }

    @Override
    public void updateState(double stepTime) {
        super.updateState(stepTime);

        if (buoyHelper != null) {
            try {
                buoyHelper.handleBuoyancy((float) stepTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void createBuoyHelper() {
        this.buoyHelper = new BuoyHelper(ocean, this);
    }

    public BuoyHelper getBuoyHelper() {
        return buoyHelper;
    }
}
