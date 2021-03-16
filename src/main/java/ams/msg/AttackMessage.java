package ams.msg;

/**
 * @Author: gq
 * @Date: 2021/3/16 10:22
 */
public class AttackMessage extends AgentMessage{

    private float damage;

    public AttackMessage() {
        super(AttackMessage.class);
    }

    public AttackMessage(float damage) {
        super(AttackMessage.class);
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }
}
