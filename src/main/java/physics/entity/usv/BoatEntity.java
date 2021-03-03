package physics.entity.usv;

import gui.obj.Model;
import org.ode4j.math.DMatrix3C;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;
import physics.entity.geom.CubeEntity;

/**
 * @Author Gq
 * @Date 2021/3/3 14:59
 * @Version 1.0
 **/
public class BoatEntity extends Entity {
    private final Logger logger = LoggerFactory.getLogger(BoatEntity.class);

    private final Model model;

    public BoatEntity(DWorld world, DSpace space, float[] translation, float[] rotation, float[] scale, Model model) {
        super(world, space, translation, rotation, scale);
        this.model = model;

        init();
    }

    private void init() {
        DMass mass = OdeHelper.createMass();
        float density = 1f;

        DTriMeshData meshData = OdeHelper.createTriMeshData();
        meshData.build(model.getVertices(), model.getIndices());

        geom = OdeHelper.createTriMesh(space, meshData, null, null, null);

        mass.setTrimesh(density, (DTriMesh) geom);

        body = OdeHelper.createBody(world);
        body.setPosition(translation[0], translation[1], translation[2]);
        body.setMass(mass);
        geom.setBody(body);
    }

}
