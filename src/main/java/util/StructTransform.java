package util;

import org.joml.*;
import org.ode4j.math.*;

/**
 * @Author Gq
 * @Date 2021/3/5 14:48
 * @Version 1.0
 **/
public class StructTransform {

    public static Vector3f transformToVector3f(DVector3C vector){
        Vector3f dest = new Vector3f();
        return transformToVector3f(vector, dest);
    }

    public static Vector3f transformToVector3f(DVector3C vector, Vector3f dest){
        dest.x = (float) vector.get0();
        dest.y = (float) vector.get1();
        dest.z = (float) vector.get2();
        return dest;
    }

    public static DVector3 transformFromVector3f(Vector3f vector3f) {
        DVector3 dest = new DVector3();
        return transformFromVector3f(vector3f, dest);
    }

    public static DVector3 transformFromVector3f(Vector3f vector3f, DVector3 dest) {
        dest.set0(vector3f.x);
        dest.set1(vector3f.y);
        dest.set2(vector3f.z);
        return dest;
    }

    public static Quaternionf transformToQuaternionf(DQuaternionC quaternion){
        Quaternionf dest = new Quaternionf();
        return transformToQuaternionf(quaternion, dest);
    }

    public static Quaternionf transformToQuaternionf(DQuaternionC quaternion, Quaternionf dest){
        //四元数var0是实部
        dest.w = (float) quaternion.get0();
        dest.x = (float) quaternion.get1();
        dest.y = (float) quaternion.get2();
        dest.z = (float) quaternion.get3();
        return dest;
    }

    public static DQuaternion transformFromQuaternionf(Quaternionf quaternion){
        DQuaternion dest = new DQuaternion();
        return transformFromQuaternionf(quaternion, dest);
    }

    public static DQuaternion transformFromQuaternionf(Quaternionf quaternion, DQuaternion dest){
        //四元数var0是实部
        dest.set0(quaternion.w);
        dest.set1(quaternion.x);
        dest.set2(quaternion.y);
        dest.set3(quaternion.z);
        return dest;
    }
}
