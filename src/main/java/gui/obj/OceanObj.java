package gui.obj;

import gui.graphic.Material;
import gui.graphic.Mesh;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * @Author Gq
 * @Date 2021/2/21 17:24
 * @Version 1.0
 **/
public class OceanObj extends GameObj{
    public OceanObj(Vector3f translation, Quaternionf rotation, Vector3f scale) {
        super("ocean", translation, rotation, scale);
    }
}
