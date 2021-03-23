package ams.msg;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.usv.BoatEngine;

/**
 * @Author Gq
 * @Date 2021/3/11 21:34
 * @Version 1.0
 **/
public class SteerMessage extends AgentMessage{

    private static final Logger logger = LoggerFactory.getLogger(SteerMessage.class);

    public enum SteerType{
        STOP(0),
        FIRST_STRAIGHT(1),
        FIRST_TURN_LEFT(2),
        FIRST_TURN_RIGHT(3),
        FIRST_TURN_HALF_LEFT(4),
        FIRST_TURN_HALF_RIGHT(5),
        SECOND_STRAIGHT(6),
        SECOND_TURN_LEFT(7),
        SECOND_TURN_RIGHT(8),
        SECOND_TURN_HALF_LEFT(9),
        SECOND_TURN_HALF_RIGHT(10),
        THIRD_STRAIGHT(11),
        THIRD_TURN_LEFT(12),
        THIRD_TURN_RIGHT(13),
        THIRD_TURN_HALF_LEFT(14),
        THIRD_TURN_HALF_RIGHT(15),

        MANUAL(99);

        private final int code;

        private static final SteerType[] TYPES = new SteerType[] {
                STOP,
                FIRST_STRAIGHT, FIRST_TURN_LEFT, FIRST_TURN_RIGHT, FIRST_TURN_HALF_LEFT, FIRST_TURN_HALF_RIGHT,
                SECOND_STRAIGHT, SECOND_TURN_LEFT, SECOND_TURN_RIGHT, SECOND_TURN_HALF_LEFT, SECOND_TURN_HALF_RIGHT,
                THIRD_STRAIGHT, THIRD_TURN_LEFT, THIRD_TURN_RIGHT, THIRD_TURN_HALF_LEFT, THIRD_TURN_HALF_RIGHT,
                MANUAL
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
    private SteerType steerType;
    public SteerMessage(float power, float angle) {
        super(SteerMessage.class);
        this.power = power;
        this.angle = angle;
        this.steerType = SteerType.MANUAL;
    }

    public SteerMessage(SteerType steerType) {
        super(SteerMessage.class);
        this.steerType = steerType;
    }

    public void adaptEngine(BoatEngine boatEngine) {
        switch (steerType) {
            case STOP: {
                power = 0;
                angle = 0;
                break;
            }
            case FIRST_STRAIGHT: {
                power = boatEngine.getMaxPow() / 3f;
                angle = 0;
                break;
            }
            case FIRST_TURN_LEFT: {
                power = boatEngine.getMaxPow() / 3f;
                angle = -boatEngine.getMaxAngle();
                break;
            }
            case FIRST_TURN_RIGHT: {
                power = boatEngine.getMaxPow() / 3f;
                angle = boatEngine.getMaxAngle();
                break;
            }
            case FIRST_TURN_HALF_LEFT: {
                power = boatEngine.getMaxPow() / 3f;
                angle = -boatEngine.getMaxAngle() / 2f;
                break;
            }
            case FIRST_TURN_HALF_RIGHT: {
                power = boatEngine.getMaxPow() / 3f;
                angle = boatEngine.getMaxAngle() / 2f;
                break;
            }
            case SECOND_STRAIGHT: {
                power = boatEngine.getMaxPow() * 2f / 3f;
                angle = 0;
                break;
            }
            case SECOND_TURN_LEFT: {
                power = boatEngine.getMaxPow() * 2f / 3f;
                angle = -boatEngine.getMaxAngle();
                break;
            }
            case SECOND_TURN_RIGHT: {
                power = boatEngine.getMaxPow() * 2f / 3f;
                angle = boatEngine.getMaxAngle();
                break;
            }
            case SECOND_TURN_HALF_LEFT: {
                power = boatEngine.getMaxPow() * 2f / 3f;
                angle = -boatEngine.getMaxAngle() / 2f;
                break;
            }
            case SECOND_TURN_HALF_RIGHT: {
                power = boatEngine.getMaxPow() * 2f / 3f;
                angle = boatEngine.getMaxAngle() / 2f;
                break;
            }
            case THIRD_STRAIGHT: {
                power = boatEngine.getMaxPow();
                angle = 0;
                break;
            }
            case THIRD_TURN_LEFT: {
                power = boatEngine.getMaxPow();
                angle = -boatEngine.getMaxAngle();
                break;
            }
            case THIRD_TURN_RIGHT: {
                power = boatEngine.getMaxPow();
                angle = boatEngine.getMaxAngle();
                break;
            }
            case THIRD_TURN_HALF_LEFT: {
                power = boatEngine.getMaxPow();
                angle = -boatEngine.getMaxAngle() / 2f;
                break;
            }
            case THIRD_TURN_HALF_RIGHT: {
                power = boatEngine.getMaxPow();
                angle = boatEngine.getMaxAngle() / 2f;
                break;
            }
            default:
                break;
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
