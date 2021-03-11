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
    private final DGeom geom;
    private final DBody body;
    private final ModifyBoatMesh modifyBoatMesh;

    public BuoyHelper(Ocean ocean, Entity entity) {
        this.ocean = ocean;
        this.world = entity.getWorld();
        this.geom = entity.getGeom();
        this.body = entity.getBody();
        this.modifyBoatMesh = new ModifyBoatMesh(entity, ocean);
    }

    public void handleBuoyancy(float stepTime) throws Exception{
        modifyBoatMesh.generateUnderwaterMesh();

        if (modifyBoatMesh.getUnderSurfaceTriangleData().size() > 0) {
            addUnderWaterForce(stepTime);
        }

//        if (modifyBoatMesh.getAboveSurfaceTriangleData().size() > 0) {
//            addAboveWaterForce();
//        }

//        if (geom instanceof DBox) {
//            processBuoys(geom, generateBuoys(3, (DBox) geom), getVolume((DBox) geom), getArea((DBox) geom),world);
//        } else if (geom instanceof DSphere) {
//            processBuoys(geom, generateBuoys((DSphere) geom), getVolume((DSphere) geom),getArea((DSphere) geom),world);
//        } else if (geom instanceof DCapsule) {
//            processBuoys(geom, generateBuoys(2, (DCapsule) geom), getVolume((DCapsule) geom), getArea((DCapsule) geom),world);
//        } else if (geom instanceof DCylinder) {
//            processBuoys(geom, generateBuoys(5, (DCylinder) geom), getVolume((DCylinder) geom), getArea((DCylinder) geom),world);
//        }else if (geom instanceof DTriMesh) {
//            processBuoys(geom, generateBuoys(5, (DCylinder) geom),getVolume((DCylinder) geom), getArea((DCylinder) geom),world);
//        }
    }

    public ModifyBoatMesh getModifyBoatMesh() {
        return modifyBoatMesh;
    }

    private void addUnderWaterForce(float stepTime) {
        Vector3f velocity = transformToVector3f(body.getLinearVel());
        Vector3f normal = new Vector3f(velocity);
        normal.normalize();
        float len = modifyBoatMesh.calcUnderwaterLength(normal);
        float Cf = resistanceCoefficient(
                velocity.length(),
                len);

        SlammingForceData[] slammingForceData = modifyBoatMesh.getSlammingForceData();

        calcSlammingVelocities(slammingForceData);

        float boatArea = modifyBoatMesh.getTotalArea();
        float boatMass = (float) body.getMass().getMass();

        List<Integer> indexOfOriginalTriangle = modifyBoatMesh.getIndexOfOriginalTriangle();

        List<TriangleData> underSurfaceTriangleData = modifyBoatMesh.getUnderSurfaceTriangleData();

        for (int i = 0; i < underSurfaceTriangleData.size(); i++) {

            TriangleData triangleData = underSurfaceTriangleData.get(i);

            int originalTriangleIndex = indexOfOriginalTriangle.get(i);
            SlammingForceData slammingData = slammingForceData[originalTriangleIndex];
            Vector3f force = new Vector3f();
            Vector3f buoyancyForce = buoyancyForce(RHO_OCEAN_WATER, triangleData);
            logger.debug("bF {}", buoyancyForce.length());
            Vector3f viscousWaterResistanceForce = viscousWaterResistanceForce(RHO_OCEAN_WATER, triangleData, Cf);
            logger.debug("vF {}", viscousWaterResistanceForce.length());
            Vector3f pressureDragForce = pressureDragForce(triangleData);
            logger.debug("pF {}", pressureDragForce.length());
            Vector3f slammingForce = slammingForce(slammingData, triangleData, boatArea, boatMass, stepTime);
            logger.debug("sF {}", slammingForce.length());
            logger.debug("tV {}", triangleData.getVelocity().length());
            logger.debug("bV {}", body.getLinearVel().length());
            force.add(buoyancyForce);
            force.add(viscousWaterResistanceForce);
            force.add(pressureDragForce);
            force.add(slammingForce);
            Vector3f forcePos = triangleData.getCenter();
            body.addForceAtPos(transformFromVector3f(force), transformFromVector3f(forcePos));
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
            body.addForceAtPos(transformFromVector3f(force), transformFromVector3f(forcePos));
        }
    }

    private void calcSlammingVelocities(SlammingForceData[] slammingForceData) {
        for (SlammingForceData data : slammingForceData) {
            data.setPreviousVelocity(data.getVelocity());
            Vector3f center = data.getTriangleCenter();
            data.setVelocity(PhysicsMath.triangleVelocity(geom,
                    modifyBoatMesh.getTransform().transformPoint(center.x, center.y, center.z)));
        }
    }
}
