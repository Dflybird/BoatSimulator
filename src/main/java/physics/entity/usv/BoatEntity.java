package physics.entity.usv;

import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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

    public BoatEntity(DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        super(world, space, translation, rotation, scale, model);

        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        float density = 0.8f;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        meshData.build(model.getVertices(), model.getIndices());

        geom = OdeHelper.createTriMesh(space, meshData, null, null, null);

        mass.setTrimesh(density, (DTriMesh) geom);

        body = OdeHelper.createBody(world);
        body.setPosition(transformFromVector3f(translation));
        body.setMass(mass);
        geom.setBody(body);
    }

}
