package gui.obj;

import gui.graphic.Mesh;
import org.joml.Vector3f;

import java.util.UUID;

/**
 * @Author Gq
 * @Date 2020/12/10 20:58
 * @Version 1.0
 **/
public abstract class GameObj {

    private final String uuid;

    protected Vector3f translation;
    protected Vector3f rotation;
    protected float scale;
    protected Mesh mesh;

    public GameObj(Vector3f translation, Vector3f rotation, float scale) {
        this(UUID.randomUUID().toString(), translation, rotation, scale);
    }

    public GameObj(String uuid, Vector3f translation, Vector3f rotation, float scale) {
        this.uuid = uuid;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

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

    public String getID() {
        return uuid;
    }
}
