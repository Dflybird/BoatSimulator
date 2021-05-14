package ams.msg;


import ams.AgentManager;
import ams.agent.usv.USVAgent;
import conf.Constant;
import conf.SceneConfig;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import physics.entity.usv.BoatEngine;
import util.AgentUtil;

import java.util.*;

/**
 * @Author Gq
 * @Date 2021/3/11 21:34
 * @Version 1.0
 **/
public class SteerMessage extends AgentMessage {

    private static final Logger logger = LoggerFactory.getLogger(SteerMessage.class);

    public enum ControllerType {
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

        private static final ControllerType[] TYPES = new ControllerType[]{
                STOP,
                FIRST_STRAIGHT, FIRST_TURN_LEFT, FIRST_TURN_RIGHT, FIRST_TURN_HALF_LEFT, FIRST_TURN_HALF_RIGHT,
                SECOND_STRAIGHT, SECOND_TURN_LEFT, SECOND_TURN_RIGHT, SECOND_TURN_HALF_LEFT, SECOND_TURN_HALF_RIGHT,
                THIRD_STRAIGHT, THIRD_TURN_LEFT, THIRD_TURN_RIGHT, THIRD_TURN_HALF_LEFT, THIRD_TURN_HALF_RIGHT,
                MANUAL
        };

        ControllerType(int code) {
            this.code = code;
        }

        public static ControllerType typeOf(int code) {
            return TYPES[code];
        }

        public int toInteger() {
            return code;
        }
    }

    private float power;
    private float angle;
    private ControllerType controllerType;

    private int steerType;
    private int throttleType;

    public SteerMessage(float power, float angle) {
        super(SteerMessage.class);
        this.power = power;
        this.angle = angle;
        this.controllerType = ControllerType.MANUAL;
    }

    public SteerMessage(ControllerType controllerType) {
        super(SteerMessage.class);
        this.controllerType = controllerType;
    }

    public SteerMessage(int steerType, int throttleType) {
        super(SteerMessage.class);
        this.steerType = steerType;
        this.throttleType = throttleType;
    }

    public void adaptEngine(USVAgent agent) {
        BoatEngine boatEngine = agent.getEngine();
        if (steerType == 9) {
            steerType = guard(agent);
        }
        switch (steerType) {
            case 0:
                angle = 0;
                break;
            case 1:
                angle = -boatEngine.getMaxAngle() / 4;
                break;
            case 2:
                angle = -boatEngine.getMaxAngle() / 2;
                break;
            case 3:
                angle = -boatEngine.getMaxAngle() * 3 / 4;
                break;
            case 4:
                angle = -boatEngine.getMaxAngle();
                break;
            case 5:
                angle = boatEngine.getMaxAngle() / 4;
                break;
            case 6:
                angle = boatEngine.getMaxAngle() / 2;
                break;
            case 7:
                angle = boatEngine.getMaxAngle() * 3 / 4;
                break;
            case 8:
                angle = boatEngine.getMaxAngle();
                break;
        }
        switch (throttleType) {
            case 0:
                power = 0;
                break;
            case 1:
                power = boatEngine.getMaxPow() / 4;
                break;
            case 2:
                power = boatEngine.getMaxPow() / 2;
                break;
            case 3:
                power = boatEngine.getMaxPow() * 3 / 4;
                break;
            case 4:
                power = boatEngine.getMaxPow();
                break;
        }
    }

//    public void adaptEngine(BoatEngine boatEngine) {
//        switch (controllerType) {
//            case STOP: {
//                power = 0;
//                angle = 0;
//                break;
//            }
//            case FIRST_STRAIGHT: {
//                power = boatEngine.getMaxPow() / 3f;
//                angle = 0;
//                break;
//            }
//            case FIRST_TURN_LEFT: {
//                power = boatEngine.getMaxPow() / 3f;
//                angle = -boatEngine.getMaxAngle();
//                break;
//            }
//            case FIRST_TURN_RIGHT: {
//                power = boatEngine.getMaxPow() / 3f;
//                angle = boatEngine.getMaxAngle();
//                break;
//            }
//            case FIRST_TURN_HALF_LEFT: {
//                power = boatEngine.getMaxPow() / 3f;
//                angle = -boatEngine.getMaxAngle() / 2f;
//                break;
//            }
//            case FIRST_TURN_HALF_RIGHT: {
//                power = boatEngine.getMaxPow() / 3f;
//                angle = boatEngine.getMaxAngle() / 2f;
//                break;
//            }
//            case SECOND_STRAIGHT: {
//                power = boatEngine.getMaxPow() * 2f / 3f;
//                angle = 0;
//                break;
//            }
//            case SECOND_TURN_LEFT: {
//                power = boatEngine.getMaxPow() * 2f / 3f;
//                angle = -boatEngine.getMaxAngle();
//                break;
//            }
//            case SECOND_TURN_RIGHT: {
//                power = boatEngine.getMaxPow() * 2f / 3f;
//                angle = boatEngine.getMaxAngle();
//                break;
//            }
//            case SECOND_TURN_HALF_LEFT: {
//                power = boatEngine.getMaxPow() * 2f / 3f;
//                angle = -boatEngine.getMaxAngle() / 2f;
//                break;
//            }
//            case SECOND_TURN_HALF_RIGHT: {
//                power = boatEngine.getMaxPow() * 2f / 3f;
//                angle = boatEngine.getMaxAngle() / 2f;
//                break;
//            }
//            case THIRD_STRAIGHT: {
//                power = boatEngine.getMaxPow();
//                angle = 0;
//                break;
//            }
//            case THIRD_TURN_LEFT: {
//                power = boatEngine.getMaxPow();
//                angle = -boatEngine.getMaxAngle();
//                break;
//            }
//            case THIRD_TURN_RIGHT: {
//                power = boatEngine.getMaxPow();
//                angle = boatEngine.getMaxAngle();
//                break;
//            }
//            case THIRD_TURN_HALF_LEFT: {
//                power = boatEngine.getMaxPow();
//                angle = -boatEngine.getMaxAngle() / 2f;
//                break;
//            }
//            case THIRD_TURN_HALF_RIGHT: {
//                power = boatEngine.getMaxPow();
//                angle = boatEngine.getMaxAngle() / 2f;
//                break;
//            }
//            default:
//                break;
//        }
//    }

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

    private int guard(USVAgent agent) {
        SceneConfig sceneConfig = SceneConfig.loadConfig();
        String mainShipId = AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, sceneConfig.getMainShip().getId());
        USVAgent mainUSV = (USVAgent) AgentManager.getAgent(mainShipId);
        Vector3f refPos = new Vector3f(mainUSV.getEntity().getTranslation());
        Vector3f direction = new Vector3f(mainUSV.getCurrForward());
        List<Vector3f> scouts = addJustGuard(refPos, 3, sceneConfig.getAllyDetectRange() * 1.1f, direction);

        Map<String[], Double> costs = new HashMap<>();
        int col;
        for (Vector3f v : scouts) {
            col = scouts.indexOf(v);
            double distance = v.distance(agent.getEntity().getTranslation());
            Quaternionf rot = fromTwoVectors(agent.getCurrForward(), v);
            double or = getYaw(rot) / 180;
            double angle =Math.abs(or);
            double cost = distance + angle;
            costs.put(new String[]{agent.getAgentID(), col + ""}, cost);
        }
        List<Map.Entry<String[], Double>> list = new LinkedList<>(costs.entrySet());
        list.sort(Map.Entry.comparingByValue());

        for (Vector3f v: scouts) {
            col=scouts.indexOf(v);
            for (Map.Entry<String[], Double> aa : list) {
                String[] ass = aa.getKey();
                if (ass[1].equals(col+"")) {
                    return steer(v, new Vector3f(agent.getEntity().getTranslation()), agent.getCurrForward());
                }

            }
        }

        return 0;
    }

    private List<Vector3f> addJustGuard(Vector3f ref, int number_of_points, float dist_apart, Vector3f direction) {
        List<Vector3f> gen = new ArrayList<>();
        if (number_of_points < 1) return gen;
        float angularDistance = 360f / number_of_points;
        for (float ang = 0; ang < 360; ang += angularDistance) {
            Vector3f right = new Vector3f(direction);
            right.rotateY(ang);
            Vector3f to = new Vector3f();
            ref.add(right.normalize().mul(new Vector3f(dist_apart, dist_apart, 1)), to);
            gen.add(to);
        }
        return gen;
    }

    public int steer(Vector3f nextTarget, Vector3f position, Vector3f forward) {
        Vector3f direction = new Vector3f();
        nextTarget.sub(position, direction);

        double directionAngle = Math.atan2(direction.z, direction.x) + Math.PI;
        double forwardAngle = Math.atan2(forward.z, forward.x) + Math.PI;

        int action;
        double offsetAngle = (forwardAngle - directionAngle + Math.PI * 2) % (Math.PI * 2);
        if (offsetAngle > 90f / 180f * Math.PI && offsetAngle <= Math.PI) {
            action = 4;
        } else if (offsetAngle >= Math.PI && offsetAngle < 270f / 180f * Math.PI) {
            action = 8;
        } else if (offsetAngle > 30f / 180f * Math.PI && offsetAngle <= 90f / 180f * Math.PI) {
            action = 3;
        } else if (offsetAngle >= 270f / 180f * Math.PI && offsetAngle < 330f / 180f * Math.PI) {
            action = 7;
        } else if (offsetAngle > 5f / 180f * Math.PI && offsetAngle <= 30f / 180f * Math.PI) {
            action = 2;
        } else if (offsetAngle >= 330f / 180f * Math.PI && offsetAngle < 355f / 180f * Math.PI) {
            action = 6;
        } else if (offsetAngle > 0 && offsetAngle <= 5f / 180f * Math.PI) {
            action = 1;
        } else if (offsetAngle >= 355f / 180f * Math.PI && offsetAngle < 360f / 180f * Math.PI) {
            action = 5;
        } else {
            action = 0;
        }
        return action;
    }

    private static Quaternionf fromTwoVectors(Vector3f u, Vector3f v) {
        u = u.normalize();
        v = v.normalize();
        float norm_u_norm_v = (float) Math.sqrt(u.dot(u)) * (v.dot(v));
        float real_p = norm_u_norm_v + u.dot(v);
        Vector3f w = new Vector3f();
        if (real_p < 1.e-6f * norm_u_norm_v) {
            /* If u and v are exactly opposite, rotate 180 degrees
             * around an arbitrary orthogonal axis. Axis normalisation
             * can happen later, when we normalise the quaternion. */
            real_p = 0.0f;
            w = Math.abs(u.x) > Math.abs(u.z) ? new Vector3f(-u.y, u.x, 0.f) : new Vector3f(0.f, -u.z, u.y);
        } else {
            u.cross(v, w);
        }
        return new Quaternionf(w.x, w.y, w.z, real_p).normalize();
    }

    private static double getYaw(Quaternionf q) {
        final float t = q.y * q.x + q.z * q.w;
        int pole = t > 0.499f ? 1 : (t < -0.499f ? -1 : 0);
        double rollRad = pole == 0 ?
                Math.atan2(2f * (q.y * q.w + q.x * q.z), 1f - 2f * (q.y * q.y + q.x * q.x)) : 0;
        return Math.toDegrees(rollRad);
    }
}
