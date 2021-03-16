package gui.obj.usv;

import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.graphic.Texture;
import gui.obj.GameObj;
import gui.obj.Model;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.File;
import java.util.UUID;

import static conf.Constant.*;

/**
 * @Author Gq
 * @Date 2021/1/30 15:26
 * @Version 1.0
 **/
public class BoatObj extends GameObj {

    private Model model;
    protected Vector3f initTranslation;
    protected Quaternionf initRotation;
    protected Vector3f initScale;

    public BoatObj(String id, Vector3f position, Quaternionf rotation, Vector3f scale, Model model) {
        super(id, position, rotation, scale);
        this.model = model;

        Material material = new Material();
        Mesh mesh = new Mesh(model, material);
        setMesh(mesh);
    }

    public void init() {
        translation = initTranslation;
        rotation = initRotation;
        scale = initScale;
    }

}
