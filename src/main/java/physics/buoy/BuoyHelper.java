package physics.buoy;

import environment.Ocean;
import org.joml.Vector3f;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;
import util.PhysicsMath;

import java.util.List;

import static conf.Constant.*;
import static util.PhysicsMath.*;
import static util.StructTransform.*;
import static util.StructTransform.transformFromVector3f;

public class BuoyHelper {
    private static final Logger logger = LoggerFactory.getLogger(BuoyHelper.class);

    private Ocean ocean;
    private final DWorld world;
    private final Entity entity;
    private final ModifyBoatMesh modifyBoatMesh;

    private final Vector3f tempBuoyancyForce = new Vector3f();
    private final Vector3f tempDampForce = new Vector3f();

    public BuoyHelper(Ocean ocean, Entity entity) {
        this.ocean = ocean;
        this.world = entity.getWorld();
        this.entity = entity;
        this.modifyBoatMesh = new ModifyBoatMesh(entity, ocean);
    }

    public void handleBuoyancy(float stepTime) throws Exception{
        modifyBoatMesh.generateUnderwaterMesh();

        if (modifyBoatMesh.getUnderSurfaceTriangleData().size() > 0) {
            addUnderWaterForce(stepTime);
        }

        if (modifyBoatMesh.getAboveSurfaceTriangleData().size() > 0) {
            addAboveWaterForce();
        }
    }

    public ModifyBoatMesh getModifyBoatMesh() {
        return modifyBoatMesh;
    }

    private void addUnderWaterForce(float stepTime) {
        Vector3f velocity = transformToVector3f(entity.getBody().getLinearVel());
        Vector3f normal = new Vector3f(velocity);
        normal.normalize();
        float len = modifyBoatMesh.calcUnderwaterLength(normal);
        float Cf = resistanceCoefficient(
                velocity.length()*2,
                len);

        SlammingForceData[] slammingForceData = modifyBoatMesh.getSlammingForceData();

        calcSlammingVelocities(slammingForceData);

        float boatArea = modifyBoatMesh.getTotalArea();
        float boatMass = (float) entity.getBody().getMass().getMass();

        List<Integer> indexOfOriginalTriangle = modifyBoatMesh.getIndexOfOriginalTriangle();

        List<TriangleData> underSurfaceTriangleData = modifyBoatMesh.getUnderSurfaceTriangleData();

        for (int i = 0; i < underSurfaceTriangleData.size(); i++) {

            TriangleData triangleData = underSurfaceTriangleData.get(i);

            int originalTriangleIndex = indexOfOriginalTriangle.get(i);
            SlammingForceData slammingData = slammingForceData[originalTriangleIndex];
            Vector3f force = new Vector3f();
            Vector3f buoyancyForce = buoyancyForce(RHO_OCEAN_WATER, triangleData);
            Vector3f viscousWaterResistanceForce = viscousWaterResistanceForce(RHO_OCEAN_WATER, triangleData, Cf);
            Vector3f pressureDragForce = pressureDragForce(triangleData);
            Vector3f slammingForce = slammingForce(slammingData, triangleData, boatArea, boatMass, stepTime);
            force.add(buoyancyForce);
            force.add(viscousWaterResistanceForce);
            force.add(pressureDragForce);
            force.add(slammingForce);
            Vector3f forcePos = triangleData.getCenter();
            entity.getBody().addForceAtPos(transformFromVector3f(force), transformFromVector3f(forcePos));
            tempBuoyancyForce.set(buoyancyForce);
            tempDampForce.set(viscousWaterResistanceForce);
            tempDampForce.add(pressureDragForce);
            tempDampForce.add(slammingForce);
        }
    }

    private void addAboveWaterForce() {
        List<TriangleData> aboveSurfaceTriangleData = modifyBoatMesh.getAboveSurfaceTriangleData();

        for (int i = 0; i < aboveSurfaceTriangleData.size(); i++) {
            TriangleData triangleData = aboveSurfaceTriangleData.get(i);

            Vector3f force = new Vector3f();

            Vector3f airResistanceForce = airResistanceForce(RHO_AIR, triangleData, C_AIR);
            force.add(airResistanceForce);
            Vector3f forcePos = triangleData.getCenter();
            entity.getBody().addForceAtPos(transformFromVector3f(force), transformFromVector3f(forcePos));
        }
    }

    private void calcSlammingVelocities(SlammingForceData[] slammingForceData) {
        for (SlammingForceData data : slammingForceData) {
            data.setPreviousVelocity(data.getVelocity());
            Vector3f center = data.getTriangleCenter();
            data.setVelocity(PhysicsMath.triangleVelocity(entity.getGeom(),
                    modifyBoatMesh.getTransform().transformPoint(center.x, center.y, center.z)));
        }
    }

    public Vector3f getTempBuoyancyForce() {
        return tempBuoyancyForce;
    }

    public Vector3f getTempDampForce() {
        return tempDampForce;
    }
}
