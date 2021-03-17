package ams.msg;

/**
 * @Author: gq
 * @Date: 2021/3/16 10:22
 */
public class AttackMessage extends AgentMessage{

    private final String attacker;
    private final float damage;

    public AttackMessage(String attacker) {
        super(AttackMessage.class);
        this.attacker = attacker;
        this.damage = 0;
    }

    public AttackMessage(String attacker, float damage) {
        super(AttackMessage.class);
        this.attacker = attacker;
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    public String getAttacker() {
        return attacker;
    }
}
