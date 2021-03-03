package physics.entity;

import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;

/**
 * @Author Gq
 * @Date 2021/2/3 17:20
 * @Version 1.0
 *
 * 旋转轴取x轴，三维向量表示为(1,0,0)
 * 四元数表示为u=(0,1,0,0)实部为0，虚部x=1
 **/
public abstract class Entity {

    protected float[] translation = new float[3];
    /*朝向 可以转换为纯四元数v=(0,x,y,z) 实部为0*/
    protected float[] orientation = new float[4];
    /*旋转 四元数*/
    protected float[] rotation = new float[4];
    protected float[] scale = new float[3];

    protected DWorld world;
    protected DSpace space;

    protected DBody body;
    protected DGeom geom;

    public Entity(DWorld world, DSpace space, float[] translation, float[] rotation, float[] scale) {
        this.world = world;
        this.space = space;
        for (int i = 0; i < 3; i++) {
            this.translation[i] = translation[i];
            this.rotation[i] = rotation[i];
            this.scale[i] = scale[i];
        }
        this.rotation[3] = rotation[3];
    }

    public float[] getTranslation() {
        if (geom == null) {
            return translation;
        }
        DVector3C position = geom.getPosition();
        translation[0] = (float) position.get0();
        translation[1] = (float) position.get1();
        translation[2] = (float) position.get2();
        return translation;
    }

    public float[] getRotation() {
        if (geom == null) {
            return rotation;
        }
        //四元数var0是实部
        DQuaternionC quaternion = geom.getQuaternion();
        rotation[0] = (float) quaternion.get0();
        rotation[1] = (float) quaternion.get1();
        rotation[2] = (float) quaternion.get2();
        rotation[3] = (float) quaternion.get3();
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

    public DBody getBody() {
        return body;
    }

    public DGeom getGeom() {
        return geom;
    }

    public DWorld getWorld() {
        return world;
    }
}
