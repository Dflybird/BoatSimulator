package physics.entity.usv;

import org.joml.Vector3f;
import physics.entity.Entity;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:39
 */
public class BoatRadar {

    //雷达观测范围，单位m
    private static final float RADAR_RANGE = 100;

    private final Entity entity;
    private final Vector3f radarRelativeCoordinate;

    public BoatRadar(Entity entity, Vector3f radarRelativeCoordinate) {
        this.entity = entity;
        this.radarRelativeCoordinate = radarRelativeCoordinate;
    }
}
