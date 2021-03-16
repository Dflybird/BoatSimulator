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
    //2 * 2 * 2 m^3
    private static Logger logger = LoggerFactory.getLogger(CubeObj.class);

    public CubeObj(String id, Vector3f position, Quaternionf rotation, Vector3f scale, Model model) {
        super(id, position, rotation, scale);

        Material material = new Material();
        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }

}
