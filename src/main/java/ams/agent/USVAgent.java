package ams.agent;

/**
 * @Author Gq
 * @Date 2021/2/1 17:27
 * @Version 1.0
 **/
public class USVAgent extends Agent{

    public USVAgent(String agentID) {
        super(agentID);
    }

    @Override
    protected void update() {
        float[] rotation = entity.getRotation();
        rotation[0]+= 0.1f;
        entity.setRotation(rotation);
    }
}
