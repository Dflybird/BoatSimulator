package physics.entity.usv;

import environment.Ocean;
import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.buoy.BuoyHelper;
import physics.entity.Entity;

import static util.StructTransform.transformFromQuaternionf;
import static util.StructTransform.transformFromVector3f;

/**
 * @Author Gq
 * @Date 2021/2/22 18:30
 * @Version 1.0
 **/
public class BuoyEntity extends Entity {
    private final Logger logger = LoggerFactory.getLogger(BuoyEntity.class);

    private BuoyHelper buoyHelper;

    public BuoyEntity(Ocean ocean, DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        super(ocean, world, space, translation, new Vector3f(1,0,0), rotation, scale, model);
        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        //900 kg/m^3 = 0.9 g/cm^3
        float weight = 3000;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        meshData.build(model.getVertices(), model.getIndices());

        geom = OdeHelper.createTriMesh(space, meshData, null, null, null);

        mass.setTrimeshTotal(weight, (DTriMesh) geom);

        body = OdeHelper.createBody(world);
        body.setPosition(transformFromVector3f(translation));
        body.setQuaternion(transformFromQuaternionf(rotation));
        body.setMass(mass);
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
