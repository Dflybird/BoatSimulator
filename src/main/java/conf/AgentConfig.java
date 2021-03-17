package conf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import conf.gson.AgentConfigAdapter;
import conf.gson.SceneConfigAdapter;
import conf.gson.Vector3fAdapter;
import org.joml.Vector3f;

/**
 * @Author Gq
 * @Date 2021/3/16 19:24
 * @Version 1.0
 **/
public class AgentConfig {
    private int id;
    private Vector3f pos;
    private Vector3f forward;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vector3f getPos() {
        return pos;
    }

    public void setPos(Vector3f pos) {
        this.pos = pos;
    }

    public Vector3f getForward() {
        return forward;
    }

    public void setForward(Vector3f forward) {
        this.forward = forward;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(AgentConfig.class, new AgentConfigAdapter())
                .registerTypeAdapter(Vector3f.class, new Vector3fAdapter())
                .create();
        return gson.toJson(this);
    }
}
