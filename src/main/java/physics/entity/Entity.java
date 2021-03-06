package physics.entity;

import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.ode4j.math.DQuaternionC;
import org.ode4j.math.DVector3C;
import org.ode4j.ode.DBody;
import org.ode4j.ode.DGeom;
import org.ode4j.ode.DSpace;
import org.ode4j.ode.DWorld;

import static util.StructTransform.*;

/**
 * @Author Gq
 * @Date 2021/2/3 17:20
 * @Version 1.0
 *
 * 旋转轴取x轴，三维向量表示为(1,0,0)
 * 四元数表示为u=(0,1,0,0)实部为0，虚部x=1
 **/
public abstract class Entity {

    protected Vector3f translation;
    /*朝向 可以转换为纯四元数v=(0,x,y,z) 实部为0*/
    protected Quaternionf orientation;
    /*旋转 四元数*/
    protected Quaternionf rotation;
    protected Vector3f scale;

    protected DWorld world;
    protected DSpace space;

    protected DBody body;
    protected DGeom geom;

    protected final Model model;

    public Entity(DWorld world, DSpace space, Vector3f translation, Quaternionf rotation, Vector3f scale, Model model) {
        this.world = world;
        this.space = space;
        this.model = model;
        this.translation = new Vector3f(translation);
        this.rotation = new Quaternionf(rotation);
        this.scale = new Vector3f(scale);
    }

    public void updateState() {
        if (geom == null) {
            return;
        }
        //更新位移变化
        DVector3C position = geom.getPosition();
        transformToVector3f(position, translation);
        //更新选择变化
        DQuaternionC quaternion = geom.getQuaternion();
        transformToQuaternionf(quaternion, rotation);
    }

//    protected abstract void update();

    public Vector3f getTranslation() {
        return translation;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
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

    public Model getModel() {
        return model;
    }
}
