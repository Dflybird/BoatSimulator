package physics.entity;

/**
 * @Author Gq
 * @Date 2021/2/3 17:20
 * @Version 1.0
 **/
public abstract class Entity {

    protected float[] translation = new float[3];
    protected float[] rotation = new float[3];
    protected float[] scale = new float[3];

    public Entity(float[] translation, float[] rotation, float[] scale) {
        for (int i = 0; i < 3; i++) {
            this.translation[i] = translation[i];
            this.rotation[i] = rotation[i];
            this.scale[i] = scale[i];
        }
    }

    public float[] getTranslation() {
        return translation;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float[] getScale() {
        return scale;
    }

    public void setTranslation(float[] translation) {
        this.translation = translation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }

    public void setScale(float[] scale) {
        this.scale = scale;
    }
}
