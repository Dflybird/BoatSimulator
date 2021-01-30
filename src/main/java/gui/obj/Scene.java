package gui.obj;

import gui.graphic.Mesh;

import java.util.*;

/**
 * @Author Gq
 * @Date 2021/1/30 16:17
 * @Version 1.0
 **/
public class Scene {

    private final Map<Mesh, List<GameObj>> meshMap;

    public Scene() {
        this.meshMap = new HashMap<>();
    }

    public void setGameObj (GameObj[] objects) {
        int num = objects != null ? objects.length : 0;
        for (int i = 0; i < num; i++) {
            GameObj object = objects[i];
            Mesh mesh = object.getMesh();
            List<GameObj> list = meshMap.computeIfAbsent(mesh, key -> new ArrayList<>());
            list.add(object);
        }
    }

    public void setGameObj (GameObj object) {
        if (object == null) {
            return;
        }
        Mesh mesh = object.getMesh();
        List<GameObj> list = meshMap.computeIfAbsent(mesh, key -> new ArrayList<>());
        list.add(object);
    }

    public void setGameObj (Collection<GameObj> objects) {
        if (objects == null) {
            return;
        }
        for (GameObj object : objects) {
            Mesh mesh = object.getMesh();
            List<GameObj> list = meshMap.computeIfAbsent(mesh, key -> new ArrayList<>());
            list.add(object);
        }
    }

    public void cleanup(){
        for (Mesh mesh : meshMap.keySet()) {
            mesh.cleanup();
        }
    }

    public Map<Mesh, List<GameObj>> getAllMesh() {
        return meshMap;
    }
}
