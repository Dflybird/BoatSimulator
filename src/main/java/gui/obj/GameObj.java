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

    private final String id;

    protected Vector3f translation;
    protected Vector3f rotation;
    protected Vector3f scale;
    protected Mesh mesh;

    public GameObj(Vector3f translation, Vector3f rotation, Vector3f scale) {
        this(UUID.randomUUID().toString(), translation, rotation, scale);
    }

    public GameObj(String id, Vector3f translation, Vector3f rotation, Vector3f scale) {
        this.id = id;
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

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public String getID() {
        return id;
    }
}
