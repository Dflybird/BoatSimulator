package state;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Objects;

public class ObjStateInfo {
    private Vector3f translation;  //len = 3
    private Quaternionf rotation; //len = 4
    private Vector3f scale;

    public ObjStateInfo() {
        this.translation = new Vector3f();
        this.rotation = new Quaternionf();
        this.scale = new Vector3f();
    }

    public ObjStateInfo(ObjStateInfo objStateInfo) {
        this.translation = objStateInfo.translation;
        this.rotation = objStateInfo.rotation;
        this.scale = objStateInfo.scale;
    }

    public ObjStateInfo mul(float num) {
        translation.mul(num);
//        rotation.scale(num);
        scale.mul(num);
        return this;
    }

    public ObjStateInfo add(ObjStateInfo info) {
        translation.add(info.translation);
//        rotation.add(info.rotation);
        scale.add(info.scale);
        return this;
    }

    public ObjStateInfo sub(ObjStateInfo info) {
        translation.sub(info.translation);
//        rotation.sub(info.rotation);
        scale.sub(info.scale);
        return this;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation.set(translation);
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public void setRotation(Quaternionf rotation) {
        this.rotation.set(rotation);
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjStateInfo that = (ObjStateInfo) o;
        return Objects.equals(translation, that.translation) && Objects.equals(rotation, that.rotation) && Objects.equals(scale, that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translation, rotation, scale);
    }
}
