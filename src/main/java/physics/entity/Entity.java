package physics.entity;

import environment.Ocean;
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

    private final Vector3f initTranslation;
    private final Vector3f initForward;
    private final Quaternionf initRotation;
    private final Vector3f initScale;

    protected final Vector3f translation;
    /*朝向 可以转换为纯四元数v=(0,x,y,z) 实部为0*/
    protected final Vector3f forward;
    /*旋转 四元数*/
    protected final Quaternionf rotation;
    protected final Vector3f scale;

    protected final Vector3f linearVelocity;

    protected final DWorld world;
    protected final DSpace space;
    protected final Ocean ocean;
    protected final Model model;

    protected DBody body;
    protected  DGeom geom;

    public Entity(Ocean ocean, DWorld world, DSpace space, Vector3f translation, Vector3f forward, Quaternionf rotation, Vector3f scale, Model model) {
        this.ocean = ocean;
        this.world = world;
        this.space = space;
        this.model = model;
        this.initTranslation = new Vector3f(translation);
        this.initRotation = new Quaternionf(rotation);
        this.initScale = new Vector3f(scale);
        this.initForward = new Vector3f(forward);
        this.translation = new Vector3f(translation);
        this.rotation = new Quaternionf(rotation);
        this.scale = new Vector3f(scale);
        this.forward = new Vector3f(forward);
        this.linearVelocity = new Vector3f();
    }

    public void reset() {
        this.translation.set(initTranslation);
        this.rotation.set(initRotation);
        this.scale.set(initScale);
        this.forward.set(initForward);
    }

    public void updateState(double stepTime) {
        if (geom == null) {
            return;
        }
        //更新位移变化
        DVector3C position = geom.getPosition();
        transformToVector3f(position, translation);
        //更新选择变化
        DQuaternionC quaternion = geom.getQuaternion();
        transformToQuaternionf(quaternion, rotation);

        DVector3C linearVel = body.getLinearVel();

        transformToVector3f(linearVel, linearVelocity);
    }

    public void destroy() {
        if (body != null) {
            body.destroy();
            body = null;
        }
        if (geom != null) {
            geom.destroy();
            geom = null;
        }
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getForward() {
        return forward;
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

    public Ocean getOcean() {
        return ocean;
    }

    public Vector3f getLinearVelocity() {
        return linearVelocity;
    }
}
