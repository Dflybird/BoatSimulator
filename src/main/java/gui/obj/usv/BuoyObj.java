package gui.obj.usv;

import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.obj.GameObj;
import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Gq
 * @Date 2021/3/16 20:21
 * @Version 1.0
 **/
public class BuoyObj extends GameObj {
    private static Logger logger = LoggerFactory.getLogger(BuoyObj.class);

    public BuoyObj(String id, Vector3f position, Quaternionf rotation, Vector3f scale, Model model) {
        super(id, position, rotation, scale);

        Material material = new Material();
        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }

    public void setColor(float r, float g, float b, float a){
        mesh.getMaterial().setAmbient(new Vector4f(r,g,b,a));
        mesh.getMaterial().setDiffuse(new Vector4f(r,g,b,a));
        mesh.getMaterial().setSpecular(new Vector4f(r,g,b,a));
    }
}
