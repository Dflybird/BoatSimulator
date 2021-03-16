package gui.obj;

import gui.graphic.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @Author Gq
 * @Date 2020/12/10 20:58
 * @Version 1.0
 **/
public abstract class GameObj {
    private static final Logger logger = LoggerFactory.getLogger(GameObj.class);

    private final String id;

    protected Vector3f translation;
    protected Quaternionf rotation;
    protected Vector3f scale;
    protected Mesh mesh;
    protected boolean render;

    public GameObj(String id, Vector3f translation, Quaternionf rotation, Vector3f scale) {
        this.id = id;
        this.translation = new Vector3f(translation);
        this.rotation = new Quaternionf(rotation);
        this.scale = new Vector3f(scale);
        this.render = true;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
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

    public boolean isRender() {
        return render;
    }

    public void setRender(boolean render) {
        this.render = render;
    }
}
