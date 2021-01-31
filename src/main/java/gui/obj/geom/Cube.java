package gui.obj.geom;

import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.obj.GameObj;
import gui.obj.Model;
import org.joml.Vector3f;

import java.io.File;

import static conf.Constant.BOAT_OBJ_NAME;
import static conf.Constant.RESOURCES_MODELS_DIR;

/**
 * @Author Gq
 * @Date 2021/1/31 16:22
 * @Version 1.0
 **/
public class Cube extends GameObj {
    public Cube(Vector3f position, Vector3f rotation, float scale) {
        super(position, rotation, scale);
        Model model = Model.loadObj(new File(RESOURCES_MODELS_DIR, "cube.obj"));
        Material material = new Material();

        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }
}
