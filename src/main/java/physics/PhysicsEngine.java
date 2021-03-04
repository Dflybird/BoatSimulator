package physics;

import conf.Constant;
import org.ode4j.ode.*;

import static org.ode4j.ode.OdeConstants.dContactBounce;
import static org.ode4j.ode.OdeConstants.dContactRolling;
import static org.ode4j.ode.OdeHelper.areConnectedExcluding;

/**
 * @Author Gq
 * @Date 2021/2/22 15:45
 * @Version 1.0
 **/
public class PhysicsEngine {

    private static final int MAX_CONTACTS = 36;

    private DWorld world;
    private DSpace space;
    private DJointGroup contactGroup;

    public void init() {
        OdeHelper.initODE2(0);
        world = OdeHelper.createWorld();
        space = OdeHelper.createHashSpace(null);
        contactGroup = OdeHelper.createJointGroup();

        world.setGravity(0, -Constant.g, 0);
        world.setContactMaxCorrectingVel(2.5);
        world.setMaxAngularSpeed(1);
        world.setAngularDamping(0.6 * 0.002);
        world.setAngularDampingThreshold(0);
        world.setLinearDampingThreshold(0);
        world.setLinearDamping(0.6 * 0.002);
        //平面方程
        OdeHelper.createPlane( space, 0, 1, 0, -10 );
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

        DBody b1 = o1.getBody();
        DBody b2 = o2.getBody();
        if (b1!=null && b2!=null && areConnectedExcluding (b1,b2,DContactJoint.class)) return;

        DContactBuffer contacts = new DContactBuffer(MAX_CONTACTS);
        for (int i = 0; i < MAX_CONTACTS; i++) {
            DContact contact = contacts.get(i);
//            contact.surface.mode = OdeConstants.dContactSlip1 | OdeConstants.dContactSlip2 |
//                    OdeConstants.dContactSoftERP | OdeConstants.dContactSoftCFM | OdeConstants.dContactApprox1;
            contact.surface.mode = OdeConstants.dContactBounce | OdeConstants.dContactSoftCFM;
            contact.surface.mu = OdeConstants.dInfinity;
//            contact.surface.slip1 = 0.7;
//            contact.surface.slip2 = 0.7;
//            contact.surface.soft_erp = 0.96;
//            contact.surface.soft_cfm = 0.04;
            contact.surface.mu2 = 0;
            contact.surface.bounce = 0.1;
            contact.surface.bounce_vel = 0.1;
            contact.surface.soft_cfm = 0.01;
        }
        int contacts_num = OdeHelper.collide(o1,o2,MAX_CONTACTS,contacts.getGeomBuffer());
        if (contacts_num!=0){
            for (int i = 0; i < contacts_num; i++) {
                DContact contact = contacts.get(i);

                DJoint c = OdeHelper.createContactJoint (world,contactGroup,contact);
                c.attach(b1, b2);
            }
        }
//        DBody b1 = o1.getBody();
//        DBody b2 = o2.getBody();
//        if (b1!=null && b2!=null && areConnectedExcluding (b1,b2,DContactJoint.class)) return;
//
//        DContactBuffer contacts = new DContactBuffer(MAX_CONTACTS);   // up to MAX_CONTACTS contacts per box-box
//        for (int i=0; i<MAX_CONTACTS; i++) {
//            DContact contact = contacts.get(i);
//            contact.surface.mode = dContactBounce | dContactRolling;
//            contact.surface.mu = 250;
//            contact.surface.rho = 0.2;
//            contact.surface.bounce = 0.2;
//        }
//        int numc = OdeHelper.collide (o1,o2,MAX_CONTACTS,contacts.getGeomBuffer() );
//        if (numc != 0) {
//            for (int i=0; i<numc; i++) {
//                DContact contact = contacts.get(i);
//                DJoint c = OdeHelper.createContactJoint (world,contactGroup,contact );
//                c.attach (b1,b2);
//            }
//        }
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
