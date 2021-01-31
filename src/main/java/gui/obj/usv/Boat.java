package gui.obj.usv;

import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.graphic.Texture;
import gui.obj.GameObj;
import gui.obj.Model;
import org.joml.Vector3f;

import java.io.File;
import java.util.UUID;

import static conf.Constant.*;

/**
 * @Author Gq
 * @Date 2021/1/30 15:26
 * @Version 1.0
 **/
public class Boat extends GameObj {

    public Boat(Vector3f position, Vector3f rotation, float scale) {
        super(position, rotation, scale);
        init(position, rotation, scale);
    }

    public Boat(String id, Vector3f position, Vector3f rotation, float scale) {
        super(id, position, rotation, scale);
        init(position, rotation, scale);
    }

    private void init(Vector3f position, Vector3f rotation, float scale) {
        Model model = Model.loadObj(new File(RESOURCES_MODELS_DIR, BOAT_OBJ_NAME));
//        Texture texture = new Texture(new File(RESOURCES_MODELS_DIR, BOAT_MTL_NAME));
//        Material material = new Material(texture);
        Material material = new Material();

        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }
}
