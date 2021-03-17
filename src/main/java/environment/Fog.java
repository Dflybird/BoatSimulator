package environment;

import org.joml.Vector3f;

/**
 * @Author Gq
 * @Date 2021/2/21 15:06
 * @Version 1.0
 **/
public class Fog {
    private boolean active;
    private Vector3f colour;
    private float density;
    private float visibility;

    public static Fog NO_FLOG = new Fog(false, new Vector3f(0,0,0), 0, 0);
//    public static Fog OCEAN_FLOG = new Fog(true, new Vector3f(0.25f, 0.75f, 0.65f), 0.5f, 100);
    public static Fog OCEAN_FLOG = new Fog(true, new Vector3f(0.7f, 0.7f, 0.7f), 2f, 500);

    public Fog(boolean active, Vector3f colour, float density, float visibility) {
        this.active = active;
        this.colour = colour;
        this.density = density;
        this.visibility = visibility;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public float getDensity() {
        return density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getVisibility() {
        return visibility;
    }

    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }
}
