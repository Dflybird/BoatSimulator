package environment;

import org.joml.Vector2f;

/**
 * @Author Gq
 * @Date 2021/2/21 15:06
 * @Version 1.0
 **/
public class Wind {
    private float velocity;
    private Vector2f direction;

    public Wind(float velocity, Vector2f direction) {
        this.velocity = velocity;
        this.direction = direction.normalize();
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Vector2f getDirection() {
        return direction;
    }

    public void setDirection(Vector2f direction) {
        this.direction = direction;
    }
}
