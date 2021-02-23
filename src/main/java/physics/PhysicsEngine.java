package physics;

import conf.Constant;
import org.ode4j.ode.*;

/**
 * @Author Gq
 * @Date 2021/2/22 15:45
 * @Version 1.0
 **/
public class PhysicsEngine {

    private DWorld world;
    private DSpace space;
    private DJointGroup contactGroup;

    public void init() {
        OdeHelper.initODE2(0);
        world = OdeHelper.createWorld();
        space = OdeHelper.createHashSpace();
        contactGroup = OdeHelper.createJointGroup();

        world.setGravity(0, -Constant.g, 0);
        world.setContactMaxCorrectingVel(2.5);
        world.setMaxAngularSpeed(1);
        world.setAngularDamping(0.6 * 0.002);
        world.setAngularDampingThreshold(0);
        world.setLinearDampingThreshold(0);
        world.setLinearDamping(0.6 * 0.002);
        //平面方程
        OdeHelper.createPlane( space, 0, 1, 0, 30 );
    }

    public void update(double stepTime) {
        //计算碰撞
        space.collide(null, this::nearCallback);
        //计算一定时间步长后的物理效应
        world.quickStep(stepTime);
        contactGroup.empty();
    }

    public void cleanup() {
        contactGroup.empty ();
        contactGroup.destroy ();
        space.destroy ();
        world.destroy ();
        OdeHelper.closeODE();
    }

    private void nearCallback (Object data, DGeom o1, DGeom o2) {
        assert(o1!=null);
        assert(o2!=null);

        int max_contacts = 36;

        DContactBuffer contacts = new DContactBuffer(max_contacts);
        int contacts_num = OdeHelper.collide(o1,o2,max_contacts,contacts.getGeomBuffer());
        if (contacts_num!=0){
            for (int i = 0; i < contacts_num; i++) {
                DContact contact = contacts.get(i);


                contact.surface.mode = OdeConstants.dContactSlip1 | OdeConstants.dContactSlip2 |
                        OdeConstants.dContactSoftERP | OdeConstants.dContactSoftCFM | OdeConstants.dContactApprox1;
                contact.surface.mu = OdeConstants.dInfinity;
                contact.surface.slip1 = 0.1;
                contact.surface.slip2 = 0.1;
                contact.surface.soft_erp = 0.5;
                contact.surface.soft_cfm = 0.3;

                DJoint c = OdeHelper.createContactJoint (world,contactGroup,contact);
                c.attach(contact.geom.g1.getBody(), contact.geom.g2.getBody());
            }
        }
    }

    public DWorld getWorld() {
        return world;
    }

    public DSpace getSpace() {
        return space;
    }

    public DJointGroup getContactGroup() {
        return contactGroup;
    }
}
