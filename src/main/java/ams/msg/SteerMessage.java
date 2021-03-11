package ams.msg;

/**
 * @Author Gq
 * @Date 2021/3/11 21:34
 * @Version 1.0
 **/
public class SteerMessage extends AgentMessage{
    private float power;
    private float angle;
    public SteerMessage(float power, float angle) {
        super(SteerMessage.class);
        this.power = power;
        this.angle = angle;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }
}
