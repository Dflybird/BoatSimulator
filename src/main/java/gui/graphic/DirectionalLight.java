package gui.graphic;

import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2020/12/28 21:37
 * @Version 1.0
 **/
public class DirectionalLight {

    private final Logger logger = LoggerFactory.getLogger(DirectionalLight.class);

    private Vector3f colour;
    private Vector3f direction;
    private float intensity;

    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        this.colour = colour;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight directionalLight) {
        this(new Vector3f(directionalLight.colour), new Vector3f(directionalLight.direction), directionalLight.intensity);
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
