package gui.obj.geom;

import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.obj.GameObj;
import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static conf.Constant.BOAT_OBJ_NAME;
import static conf.Constant.RESOURCES_MODELS_DIR;

/**
 * @Author Gq
 * @Date 2021/1/31 16:22
 * @Version 1.0
 **/
public class CubeObj extends GameObj {
    private static Logger logger = LoggerFactory.getLogger(CubeObj.class);

    public CubeObj(Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(position, rotation, scale);
        init();
    }

    public CubeObj(String id, Vector3f position, Quaternionf rotation, Vector3f scale) {
        super(id, position, rotation, scale);
        init();
    }

    private void init() {
        Model model = Model.loadObj(new File(RESOURCES_MODELS_DIR, "cube.obj"));
        scale.mul(0.5f,0.5f,0.5f);
        Material material = new Material();
        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }

    @Override
    public void setScale(Vector3f scale) {
        super.setScale(scale.mul(0.5f,0.5f,0.5f));
    }
}
