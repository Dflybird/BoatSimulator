package physics.buoy;

import environment.Ocean;
import org.joml.Vector3f;
import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

import java.util.List;

import static conf.Constant.*;
import static util.StructTransform.*;

public class BuoyHelper {
    private static final Logger logger = LoggerFactory.getLogger(BuoyHelper.class);

    private Ocean ocean;
    private final DWorld world;
    private final DGeom geom;
    private final ModifyBoatMesh modifyBoatMesh;

    public BuoyHelper(Ocean ocean, Entity entity) {
        this.ocean = ocean;
        this.world = entity.getWorld();
        this.geom = entity.getGeom();
        this.modifyBoatMesh = new ModifyBoatMesh(entity, ocean);
    }

    public void handleBuoyancy() {
        modifyBoatMesh.generateUnderwaterMesh();

        if (modifyBoatMesh.getUnderSurfaceTriangleData().size() > 0) {
            addUnderWaterForce();
        }

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

    private void addUnderWaterForce() {
        List<TriangleData> underSurfaceTriangleData = modifyBoatMesh.getUnderSurfaceTriangleData();
        DBody body = geom.getBody();
        for (TriangleData triangleData : underSurfaceTriangleData) {
            Vector3f center = triangleData.getCenter();
            DVector3 buoyancyForce = transformFromVector3f(buoyancyForce(triangleData));
            DVector3 forcePos = transformFromVector3f(center);
            body.addForceAtPos(buoyancyForce, forcePos);
        }
    }

    private Vector3f buoyancyForce(TriangleData data) {
        // F_buoyancy = rho * g * V
        // rho - density of the mediaum you are in
        // g - gravity
        // V - volume of fluid directly above the curved surface
        // V = z * S * n
        // z - distance to surface
        // S - surface area
        // n - normal to the surface
        float y = -g*rho*data.getDistanceToSurface()*data.getArea()*data.getNormal().y;
        return new Vector3f(0,y,0);
    }

    private double getWaterLevel(DVector3 position) {
        return ocean.getWaveHeight((float) position.get0(), (float) position.get2()); // Waves can be simulated here
    }

    private void processBuoys(DGeom geom, Buoy[] buoys, double totalVolume, double totalArea, DWorld world) {
        double density = 1;
        DBody body = geom.getBody();
        double buoyVolume = 0;
        double buoyArea = 0;
        for (Buoy b : buoys) {
            double radius = b.radius;
            double radiusSqr = radius * radius;
            buoyVolume += b.weight * 4 * Math.PI / 3 * radius * radiusSqr;
            buoyArea += b.weight * Math.PI * radiusSqr;
        }
        double volumeRatio = totalVolume / buoyVolume;
        double areaRatio = 0.5 * totalArea / buoyArea;
        DVector3 position = new DVector3();
        for (Buoy b : buoys) {
            geom.getRelPointPos(b.x, b.y, b.z, position);
            double waterLevel = getWaterLevel(position);
            double radius = b.radius;
            double y = position.get1();
            if (y - radius < waterLevel) {
                double h = Math.max(0, Math.min(waterLevel + radius - y, 2 * radius));
                double base = Math.sqrt(h * (2 * radius - h));
                double volume = Math.PI * h / 6.0 * (3 * base * base + h * h);

                DVector3 buoyancyForce = new DVector3();
                world.getGravity(buoyancyForce);
                buoyancyForce.scale(-density * volumeRatio * volume * b.weight);

                double area = Math.PI * radius * h * areaRatio * b.weight;
                DVector3 dragForce = getDragForce(density, body, area);
                DVector3 dragTorque = getDragTorque(density, body, area);
                body.addForceAtPos(dragForce.get0() + buoyancyForce.get0(), buoyancyForce.get1() + dragForce.get1(),
                        buoyancyForce.get2() + dragForce.get2(), position.get0(), position.get1(), position.get2());
                body.addTorque(dragTorque);
            }
        }
    }

    private Buoy[] generateBuoys(DSphere geom) {
        double radius = geom.getRadius();
        return new Buoy[] { new Buoy(0, 0, 0, (float) radius, 1) };
    }

    private Buoy[] generateBuoys(int bn, DBox geom) {
        DVector3C lengths = geom.getLengths();
        double lx = lengths.get0();
        double ly = lengths.get1();
        double lz = lengths.get2();
        double d = 0.30;
        double min = Math.min(Math.min(lx, ly), lz);
        double radius = min * 0.25;
        double weight = 1;
        double radius2 = min * 0.25;
        double weight2 = 0.1;
        Buoy[] buoys = new Buoy[14];
        double d2 = 0.25;
        buoys[0] = new Buoy(-d * lx, 0, 0, radius, weight);
        buoys[1] = new Buoy(d * lx, 0, 0, radius, weight);
        buoys[2] = new Buoy(0, -d * ly, 0, radius, weight);
        buoys[3] = new Buoy(0, d * ly, 0, radius, weight);
        buoys[4] = new Buoy(0, 0, -d * lz, radius, weight);
        buoys[5] = new Buoy(0, 0, d * lz, radius, weight);

        buoys[6] = new Buoy(-lx * d2, -ly * d2, -lz * d2, radius2, weight2);
        buoys[7] = new Buoy(-lx * d2, -ly * d2, lz * d2, radius2, weight2);
        buoys[8] = new Buoy(-lx * d2, ly * d2, -lz * d2, radius2, weight2);
        buoys[9] = new Buoy(-lx * d2, ly * d2, lz * d2, radius2, weight2);
        buoys[10] = new Buoy(lx * d2, -ly * d2, -lz * d2, radius2, weight2);
        buoys[11] = new Buoy(lx * d2, -ly * d2, lz * d2, radius2, weight2);
        buoys[12] = new Buoy(lx * d2, ly * d2, -lz * d2, radius2, weight2);
        buoys[13] = new Buoy(lx * d2, ly * d2, lz * d2, radius2, weight2);

        return buoys;
    }

    private Buoy[] generateBuoys(int bn, DCapsule geom) {
        double length = geom.getLength();
        double radius = geom.getRadius();
        Buoy[] buoys = new Buoy[bn];
        int i = 0;
        for (int bz = 0; bz < bn; bz++) {
            float pz = (float) (((float) bz / (bn - 1) - 0.5) * length);
            buoys[i++] = new Buoy(0, 0, pz, (float) radius, 1);
        }
        return buoys;
    }

    private Buoy[] generateBuoys(int bn, DCylinder geom) {
        double length = geom.getLength();
        double radius = geom.getRadius();
        double bRadius = Math.min(0.25 * length, 0.5 * radius);
        int segments = 2;
        Buoy[] buoys = new Buoy[bn * segments];
        int i = 0;
        for (int bz = 0; bz < segments; bz++) {
            float pz = (float) (((float) bz / (segments - 1) - 0.5) * (length - bRadius));
            for (int bc = 0; bc < bn; bc++) {
                double a = 2 * Math.PI * bc / bn;
                float px = (float) ((radius - bRadius) * Math.cos(a));
                float py = (float) ((radius - bRadius) * Math.sin(a));
                buoys[i++] = new Buoy(px, py, pz, (float) bRadius, 1);
            }
        }
        return buoys;
    }

    private double getVolume(DSphere geom) {
        double radius = geom.getRadius();
        return 4 * Math.PI / 3 * radius * radius * radius;
    }

    private double getVolume(DBox geom) {
        DVector3C lengths = geom.getLengths();
        return lengths.get0() * lengths.get1() * lengths.get2();
    }

    private double getVolume(DCapsule geom) {
        double radius = geom.getRadius();
        return Math.PI * radius * radius * geom.getLength() + 4 * Math.PI / 3 * radius * radius * radius;
    }

    private double getVolume(DCylinder geom) {
        double radius = geom.getRadius();
        return Math.PI * radius * radius * geom.getLength();
    }

    private double getArea(DSphere geom) {
        double radius = geom.getRadius();
        return 4 * Math.PI * radius * radius;
    }

    private double getArea(DBox geom) {
        DVector3C lengths = geom.getLengths();
        return lengths.get0() * lengths.get1() + lengths.get0() * lengths.get2() + lengths.get2() * lengths.get1();
    }

    private double getArea(DCapsule geom) {
        double radius = geom.getRadius();
        return 4 * Math.PI * radius * radius + 2 * Math.PI * radius * geom.getLength();
    }

    private double getArea(DCylinder geom) {
        double radius = geom.getRadius();
        return 2 * Math.PI * radius * radius + 2 * Math.PI * radius * geom.getLength();
    }

    private DVector3 getDragForce(double density, DBody odeBody, double area) {
        DVector3 dragForce = new DVector3(odeBody.getLinearVel());
        double lvel = dragForce.length();
        dragForce.safeNormalize();
        dragForce.scale(-0.5 * density * area * lvel * lvel * 0.5);
        return dragForce;
    }

    private DVector3 getDragTorque(double density, DBody odeBody, double area) {
        DVector3 dragTorque = new DVector3(odeBody.getAngularVel());
        double avel = dragTorque.length();
        dragTorque.safeNormalize();
        dragTorque.scale(-0.5 * density * area * avel * avel * 0.5);
        return dragTorque;
    }

}
