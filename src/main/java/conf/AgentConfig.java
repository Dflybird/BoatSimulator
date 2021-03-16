package conf;

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
}
