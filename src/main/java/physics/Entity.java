package physics;

/**
 * @Author Gq
 * @Date 2021/2/3 17:20
 * @Version 1.0
 **/
public class Entity {


    private float[] translation;
    private float[] rotation;
    private float scale;

    public Entity(float[] translation, float[] rotation, float scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public float[] getTranslation() {
        return translation;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setTranslation(float[] translation) {
        this.translation = translation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
