package gui.obj;

import gui.graphic.Mesh;
import org.joml.Vector3f;

/**
 * @Author Gq
 * @Date 2020/12/10 20:58
 * @Version 1.0
 **/
public abstract class GameObj {

    protected Vector3f translation;
    protected Vector3f rotation;
    protected float scale;
    protected Mesh mesh;

    public GameObj(Vector3f translation, Vector3f rotation, float scale) {
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

    public GameObj(Vector3f translation, Vector3f rotation) {
        this(translation, rotation, 1);
    }

    public GameObj() {
        this(new Vector3f(0,0,0), new Vector3f(0,0,0));
    }

//    public abstract void render(Window window, Camera camera);

    public abstract void cleanup();

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
}
