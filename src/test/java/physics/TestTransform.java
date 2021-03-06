package physics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/3/5 16:12
 * @Version 1.0
 **/
public class TestTransform {

    private static final Logger logger = LoggerFactory.getLogger(TestTransform.class);

    @Test
    public void testTransform(){
        Quaternionf rotation = new Quaternionf();
        Quaternionf rotationConj = new Quaternionf();
        rotation.rotate((float) (Math.PI/2), 0, 0);
        rotation.conjugate(rotationConj);
        Vector3f translation = new Vector3f(10,0,0);
        Vector3f scale = new Vector3f(10,10,10);
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity().translate(translation)
                .rotate(rotation)
                .scale(scale);
        Matrix4f matrix4fInv = new Matrix4f();
        matrix4f.invert(matrix4fInv);
        Vector4f point1 = new Vector4f(10,10, 0, 1);
        point1.mul(matrix4f);
        Vector4f point2 = new Vector4f(point1);
        point2.mul(matrix4fInv);


        Vector3f point3 = new Vector3f(10,10, 0);
        point3.add(translation);
        point3.rotate(rotation);
        Vector3f point4 = new Vector3f(point3);
        point4.rotate(rotationConj);
        point4.sub(translation);
        logger.debug("point1 x:{} | y: {} | z: {}", point1.x, point1.y, point1.z);
        logger.debug("point2 x:{} | y: {} | z: {}", point2.x, point2.y, point2.z);
        logger.debug("point3 x:{} | y: {} | z: {}", point3.x, point3.y, point3.z);
        logger.debug("point3 x:{} | y: {} | z: {}", point3.x, point3.y, point3.z);
    }
}
