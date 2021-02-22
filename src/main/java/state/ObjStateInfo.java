package state;

import java.util.Arrays;
import java.util.Objects;

public class ObjStateInfo {
    private float[] translation = new float[3];  //len = 3
    private float[] rotation = new float[3]; //len = 3
    private float scale = 0;

    public ObjStateInfo() {
    }

    public ObjStateInfo(ObjStateInfo objStateInfo) {
        this.translation = objStateInfo.translation;
        this.rotation = objStateInfo.rotation;
        this.scale = objStateInfo.scale;
    }

    public ObjStateInfo mul(double num) {
        for (int i = 0; i < 3; i++) {
            translation[i] *= num;
            rotation[i] *= num;
        }
        scale *= num;
        return this;
    }

    public ObjStateInfo add(ObjStateInfo info) {
        for (int i = 0; i < 3; i++) {
            translation[i] += info.translation[i];
            rotation[i] += info.rotation[i];
        }
        scale += info.scale;
        return this;
    }

    public ObjStateInfo sub(ObjStateInfo info) {
        for (int i = 0; i < 3; i++) {
            translation[i] -= info.translation[i];
            rotation[i] -= info.rotation[i];
        }
        scale -= info.scale;
        return this;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjStateInfo objStateInfo = (ObjStateInfo) o;
        return Float.compare(objStateInfo.scale, scale) == 0 && Arrays.equals(translation, objStateInfo.translation) && Arrays.equals(rotation, objStateInfo.rotation);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(scale);
        result = 31 * result + Arrays.hashCode(translation);
        result = 31 * result + Arrays.hashCode(rotation);
        return result;
    }
}
