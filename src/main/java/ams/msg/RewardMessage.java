package ams.msg;

/**
 * @Author: gq
 * @Date: 2021/3/17 15:04
 */
public class RewardMessage extends AgentMessage {

    private final float reward;

    public RewardMessage(float reward) {
        super(RewardMessage.class);
        this.reward = reward;
    }

    public float getReward() {
        return reward;
    }
}
