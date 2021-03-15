package ams.msg;


import conf.Constant;

/**
 * @Author Gq
 * @Date 2021/3/11 21:34
 * @Version 1.0
 **/
public class SteerMessage extends AgentMessage{

    public enum SteerType{
        STOP(0),
        STRAIGHT(1),
        TURN_LEFT(2),
        TURN_RIGHT(3);

        private final int code;

        private static final SteerType[] TYPES = new SteerType[] {
                STOP, STRAIGHT, TURN_LEFT, TURN_RIGHT
        };

        SteerType(int code) {
            this.code = code;
        }

        public static SteerType typeOf(int code) {
            return TYPES[code];
        }

        public int toInteger() {
            return code;
        }
    }

    private float power;
    private float angle;
    public SteerMessage(float power, float angle) {
        super(SteerMessage.class);
        this.power = power;
        this.angle = angle;
    }

    public SteerMessage(SteerType steerType) {
        super(SteerMessage.class);
        switch (steerType) {
            case STOP: {
                power = 0;
                angle = 0;
                break;
            }
            case STRAIGHT: {
                power = Constant.MAX_POWER;
                angle = 0;
                break;
            }
            case TURN_LEFT: {
                power = Constant.MAX_POWER;
                angle = -Constant.MAX_ANGLE;
                break;
            }
            case TURN_RIGHT: {
                power = Constant.MAX_POWER;
                angle = Constant.MAX_ANGLE;
                break;
            }
        }
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
