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
public class CubeObj extends GameObj {
    public CubeObj(Vector3f position, Vector3f rotation, Vector3f scale) {
        super(position, rotation, scale);
        init(position, rotation, scale);
    }

    public CubeObj(String id, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(id, position, rotation, scale);
        init(position, rotation, scale);
    }

    private void init(Vector3f position, Vector3f rotation, Vector3f scale) {
        Model model = Model.loadObj(new File(RESOURCES_MODELS_DIR, "cube.obj"));
        Material material = new Material();

        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }
}
