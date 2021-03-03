package physics;

import org.ode4j.math.DVector3;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.*;
import org.ode4j.ode.internal.DxGeom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.Entity;

public class BuoyHelper {
    private static final Logger logger = LoggerFactory.getLogger(BuoyHelper.class);

    public void handleBuoyancy(Entity entity) {
        DWorld world = entity.getWorld();
        DGeom geom = entity.getGeom();

        if (geom instanceof DBox) {
            logger.debug("==============Processing box geom=============");
            processBuoys(geom, generateBuoys(3, (DBox) geom), getVolume((DBox) geom), getArea((DBox) geom),world);
        } else if (geom instanceof DSphere) {
            logger.debug("==============Processing sphere geom=============");
            processBuoys(geom, generateBuoys((DSphere) geom), getVolume((DSphere) geom),getArea((DSphere) geom),world);
        } else if (geom instanceof DCapsule) {
            logger.debug("==============Processing capsule geom=============");
            processBuoys(geom, generateBuoys(2, (DCapsule) geom), getVolume((DCapsule) geom), getArea((DCapsule) geom),world);
        } else if (geom instanceof DCylinder) {
            logger.debug("==============Processing cylinder geom=============");
            processBuoys(geom, generateBuoys(5, (DCylinder) geom), getVolume((DCylinder) geom), getArea((DCylinder) geom),world);
        }else if (geom instanceof DTriMesh) {
            logger.debug("==============Processing mesh geom=============");
            processBuoys(geom, generateBuoys(5, (DCylinder) geom),getVolume((DCylinder) geom), getArea((DCylinder) geom),world);
        }
    }

    public void handleBuoyancy(Iterable<DxGeom> geoms, DWorld world) {
        // List<DGeom> floatingGeoms = new ArrayList<>();
        for (DGeom geom : geoms) {
            if (geom instanceof DBox) {
                //    System.out.println("==============Processing box geom=============");
                Buoy[] buoys = generateBuoys(3, (DBox) geom);
                processBuoys(geom, buoys, getVolume((DBox) geom), getArea((DBox) geom),world);
            } else if (geom instanceof DSphere) {
                System.out.println("==============Processing sphere geom=============");
                processBuoys(geom, generateBuoys((DSphere) geom), getVolume((DSphere) geom),getArea((DSphere) geom),world);
            } else if (geom instanceof DCapsule) {
                System.out.println("==============Processing capsule geom=============");
                processBuoys(geom, generateBuoys(2, (DCapsule) geom), getVolume((DCapsule) geom), getArea((DCapsule) geom),world);
            } else if (geom instanceof DCylinder) {
                System.out.println("==============Processing cylinder geom=============");
                processBuoys(geom, generateBuoys(5, (DCylinder) geom), getVolume((DCylinder) geom), getArea((DCylinder) geom),world);
            }else if (geom instanceof DTriMesh) {
                System.out.println("==============Processing mesh geom=============");
                processBuoys(geom, generateBuoys(5, (DCylinder) geom),getVolume((DCylinder) geom), getArea((DCylinder) geom),world);
            }
        }
        // floatingGeoms.clear();
    }

    private double getWaterLevel(DVector3 position) {
        return 40; // Waves can be simulated here
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
