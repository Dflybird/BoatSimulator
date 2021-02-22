package state;

import java.util.Arrays;
import java.util.Objects;

public class ObjStateInfo {
    private float[] translation = new float[3];  //len = 3
    private float[] rotation = new float[3]; //len = 3
    private float[] scale = new float[3];

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
            scale[i] *= num;
        }
        return this;
    }

    public ObjStateInfo add(ObjStateInfo info) {
        for (int i = 0; i < 3; i++) {
            translation[i] += info.translation[i];
            rotation[i] += info.rotation[i];
            scale[i] += info.scale[i];
        }
        return this;
    }

    public ObjStateInfo sub(ObjStateInfo info) {
        for (int i = 0; i < 3; i++) {
            translation[i] -= info.translation[i];
            rotation[i] -= info.rotation[i];
            scale[i] -= info.scale[i];
        }
        return this;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjStateInfo that = (ObjStateInfo) o;
        return Arrays.equals(translation, that.translation) && Arrays.equals(rotation, that.rotation) && Arrays.equals(scale, that.scale);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(translation);
        result = 31 * result + Arrays.hashCode(rotation);
        result = 31 * result + Arrays.hashCode(scale);
        return result;
    }
}
