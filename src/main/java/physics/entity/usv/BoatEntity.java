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

import java.util.Arrays;

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
    private float weight = 2000f;

    //船头方向就是船的朝向，指向x轴正方向
    public BoatEntity(Ocean ocean, DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        super(ocean, world, space, translation, new Vector3f(1,0,0) , rotation, scale, model);
        init();
    }

    public BoatEntity(Ocean ocean, DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, float weight,Model model) {
        super(ocean, world, space, translation, new Vector3f(1,0,0) , rotation, scale, model);
        //重量，单位kg
        this.weight = weight;
        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        float[] v = model.getVertices();
        float[] nv = new float[v.length];
        for (int i = 0; i < v.length/3; i++) {
            nv[i*3] = v[i*3] * scale.x;
            nv[i*3+1] = v[i*3+1] * scale.y;
            nv[i*3+2] = v[i*3+2] * scale.z;
        }
        meshData.build(v, model.getIndices());

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

    public Vector3f getBuoyancyForce() {
        return buoyHelper.getTempBuoyancyForce();
    }

    public Vector3f getDamp() {
        return buoyHelper.getTempDampForce();
    }
}
