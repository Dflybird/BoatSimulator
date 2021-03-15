package net;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.USVAgent;
import engine.GameLogic;
import io.grpc.stub.StreamObserver;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static conf.Constant.*;

/**
 * @Author: gq
 * @Date: 2021/3/15 15:19
 */
public class RPCServices extends AlgorithmGrpc.AlgorithmImplBase {

    private final GameLogic gameLogic;

    public RPCServices(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void getSceneParameter(Null request, StreamObserver<SceneParameter> responseObserver) {
        SceneParameter sceneParameter = SceneParameter.newBuilder()
                .setMaxVelocity(MAX_SPEED)
                .setMinVelocity(MIN_SPEED)
                .setMaxXLength(LENGTH_X * NUM_X / 2)
                .setMinXLength(-LENGTH_X * NUM_X / 2)
                .setMaxZLength(LENGTH_Z * NUM_Z / 2)
                .setMinZLength(-LENGTH_Z * NUM_Z / 2)
                .setAllyNum(ALLY_NUM)
                .setEnemyNum(ENEMY_NUM)
                .setAllyAttackRange(ALLY_ATTACK_RANGE)
                .setAllyDetectRange(ALLY_DETECT_RANGE)
                .setEnemyAttackRange(ENEMY_ATTACK_RANGE)
                .setEnemyDetectRange(ENEMY_DETECT_RANGE)
                .build();
        responseObserver.onNext(sceneParameter);
        responseObserver.onCompleted();
    }

    @Override
    public void getObservation(ObservationRequest request, StreamObserver<Observation> responseObserver) {
        List<TeamObservation> list = new ArrayList<>();
        for (int camp : request.getCampList()) {
            if (camp == USVAgent.Camp.ALLY.toInteger()) {
                list.add(getAllyObservation());
                continue;
            }
            if (camp == USVAgent.Camp.ENEMY.toInteger()) {
                list.add(getEnemyObservation());
                continue;
            }
        }
        Observation observation = Observation.newBuilder().addAllTeamObservation(list).build();
        responseObserver.onNext(observation);
        responseObserver.onCompleted();
    }

    @Override
    public void step(Action request, StreamObserver<Reward> responseObserver) {

    }

    @Override
    public void reset(Null request, StreamObserver<Null> responseObserver) {
        gameLogic.reset();
    }

    private TeamObservation getAllyObservation() {
        List<MemberObservation> list = new ArrayList<>();
        USVAgent mainShip = (USVAgent) AgentManager.getAgent(MAIN_SHIP_ID);
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (agent instanceof USVAgent) {
                USVAgent usvAgent = (USVAgent) agent;
                if (usvAgent.getCamp() == USVAgent.Camp.ALLY) {
                    MemberObservation memberObservation = MemberObservation.newBuilder()
                            .setId(usvAgent.getId())
                            .setStatus(usvAgent.getStatus().toInteger())
                            .setSelfPos(newVector3(usvAgent.getEntity().getTranslation()))
                            .setClosestEnemyPos(newVector3(usvAgent.relativeCoordinateToSelf(usvAgent.closestEnemyPos())))
                            .setClosestAllyPos(newVector3(usvAgent.relativeCoordinateToSelf(usvAgent.closestAllyPos())))
                            .setMainShipPos(newVector3(usvAgent.relativeCoordinateToSelf(mainShip.getEntity().getTranslation())))
                            .setForward(newVector3(usvAgent.getCurrForward()))
                            .build();
                    list.add(memberObservation);
                }
            }
        }
        return TeamObservation.newBuilder()
                .setCamp(USVAgent.Camp.ALLY.toInteger())
                .addAllMemberObservation(list)
                .build();
    }

    private TeamObservation getEnemyObservation() {
        List<MemberObservation> list = new ArrayList<>();
        USVAgent mainShip = (USVAgent) AgentManager.getAgent(MAIN_SHIP_ID);
        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (agent instanceof USVAgent) {
                USVAgent usvAgent = (USVAgent) agent;
                if (usvAgent.getCamp() == USVAgent.Camp.ENEMY) {
                    MemberObservation memberObservation = MemberObservation.newBuilder()
                            .setId(usvAgent.getId())
                            .setStatus(usvAgent.getStatus().toInteger())
                            .setSelfPos(newVector3(usvAgent.getEntity().getTranslation()))
                            .setClosestEnemyPos(newVector3(usvAgent.relativeCoordinateToSelf(usvAgent.closestEnemyPos())))
                            .setClosestAllyPos(newVector3(usvAgent.relativeCoordinateToSelf(usvAgent.closestAllyPos())))
                            .setMainShipPos(newVector3(usvAgent.relativeCoordinateToSelf(mainShip.getEntity().getTranslation())))
                            .setForward(newVector3(usvAgent.getCurrForward()))
                            .build();
                    list.add(memberObservation);
                }
            }
        }
        return TeamObservation.newBuilder()
                .setCamp(USVAgent.Camp.ENEMY.toInteger())
                .addAllMemberObservation(list)
                .build();
    }

    private Vector3 newVector3(Vector3f vector3f) {
        return Vector3.newBuilder()
                .setX(vector3f.x)
                .setY(vector3f.y)
                .setZ(vector3f.z)
                .build();
    }
}