package physics.entity.usv;

import org.joml.Vector3f;
import physics.entity.Entity;

/**
 * @Author: gq
 * @Date: 2021/3/12 17:50
 */
public class BoatWeapon {

    //武器攻击范围，单位m
    private static final float RADAR_RANGE = 100;

    private final Entity entity;
    private final Vector3f weaponRelativeCoordinate;

    public BoatWeapon(Entity entity, Vector3f weaponRelativeCoordinate) {
        this.entity = entity;
        this.weaponRelativeCoordinate = weaponRelativeCoordinate;
    }
}
