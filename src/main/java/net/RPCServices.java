package net;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.OnDoneAgent;
import ams.agent.usv.USVAgent;
import ams.msg.SteerMessage;
import conf.Constant;
import conf.SceneConfig;
import engine.GameLogic;
import io.grpc.stub.StreamObserver;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AgentUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static conf.Constant.*;

/**
 * @Author: gq
 * @Date: 2021/3/15 15:19
 */
public class RPCServices extends AlgorithmGrpc.AlgorithmImplBase {

    private static final Logger logger = LoggerFactory.getLogger(RPCServices.class);

    private final GameLogic gameLogic;
    private final SceneConfig sceneConfig;

    private int sizeStep = 0;

    public RPCServices(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.sceneConfig = SceneConfig.loadConfig();
    }

    @Override
    public void getSceneParameter(Null request, StreamObserver<SceneParameter> responseObserver) {
        SceneParameter sceneParameter = SceneParameter.newBuilder()
                .setMaxVelocity(MAX_SPEED)
                .setMinVelocity(MIN_SPEED)
                .setMaxXLength(sceneConfig.getMaxBoundaryX())
                .setMinXLength(sceneConfig.getMinBoundaryX())
                .setMaxZLength(sceneConfig.getMaxBoundaryZ())
                .setMinZLength(sceneConfig.getMinBoundaryZ())
                .setAllyNum(sceneConfig.getAllyNum())
                .setEnemyNum(sceneConfig.getEnemyNum())
                .setAllyAttackRange(sceneConfig.getAllyAttackRange())
                .setAllyDetectRange(sceneConfig.getAllyDetectRange())
                .setEnemyAttackRange(sceneConfig.getEnemyAttackRange())
                .setEnemyDetectRange(sceneConfig.getEnemyDetectRange())
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
        HashMap<USVAgent.Camp, Float> rewardMap = new HashMap<>();
        for (TeamAction teamAction : request.getTeamActionList()) {
            USVAgent.Camp camp = USVAgent.Camp.campOf(teamAction.getCamp());
            rewardMap.put(camp, 0f);

            for (MemberAction memberAction : teamAction.getMemberActionList()) {
                String agentID = AgentUtil.assembleName(camp, memberAction.getId());
                SteerMessage steerMessage = new SteerMessage(SteerMessage.SteerType.typeOf(memberAction.getActionType()));
                AgentManager.sendAgentMessage(agentID, steerMessage);
            }
        }

        gameLogic.play(() -> {
            for (Agent agent : AgentManager.getAgentMap().values()) {
                if (agent instanceof USVAgent) {
                    USVAgent usvAgent = (USVAgent) agent;
                    if (usvAgent.getCamp() == USVAgent.Camp.MAIN_SHIP) {
                        if (rewardMap.containsKey(USVAgent.Camp.ALLY)) {
                            float sum = rewardMap.get(USVAgent.Camp.ALLY);
                            sum += usvAgent.getReward(true);
                            rewardMap.put(USVAgent.Camp.ALLY, sum);
                        }
                    }
                    else if (rewardMap.containsKey(usvAgent.getCamp())) {
                        float sum = rewardMap.get(usvAgent.getCamp());
                        sum += usvAgent.getReward(true);
                        rewardMap.put(usvAgent.getCamp(), sum);
                    }
                }
            }
            List<TeamReward> list = new ArrayList<>(rewardMap.size());
            for (Map.Entry<USVAgent.Camp, Float> entry : rewardMap.entrySet()) {
                TeamReward teamReward = TeamReward.newBuilder()
                        .setCamp(entry.getKey().toInteger())
                        .setReward(entry.getValue())
                        .build();
                list.add(teamReward);
            }
            OnDoneAgent onDoneAgent = (OnDoneAgent) AgentManager.getAgent(ON_DONE_AGENT);
            Reward reward = Reward.newBuilder()
                    .setDone(onDoneAgent.isDone() ? 1 : 0)
                    .addAllTeamReward(list)
                    .build();
            responseObserver.onNext(reward);
            responseObserver.onCompleted();
            logger.debug("step: {}", sizeStep++);
        });
    }

    @Override
    public void reset(Null request, StreamObserver<Null> responseObserver) {
        gameLogic.reset();
        responseObserver.onNext(Null.newBuilder().build());
        responseObserver.onCompleted();
        sizeStep=0;
    }

    private TeamObservation getAllyObservation() {
        List<MemberObservation> list = new ArrayList<>();
        USVAgent mainShip = (USVAgent) AgentManager.getAgent(AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, sceneConfig.getMainShip().getId()));
        Vector3f mainShipPos = mainShip == null ? new Vector3f() : mainShip.getEntity().getTranslation();
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
                            .setMainShipPos(newVector3(usvAgent.relativeCoordinateToSelf(mainShipPos)))
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
        USVAgent mainShip = (USVAgent) AgentManager.getAgent(AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, sceneConfig.getMainShip().getId()));
        Vector3f mainShipPos = mainShip == null ? new Vector3f() : mainShip.getEntity().getTranslation();
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
                            .setMainShipPos(newVector3(usvAgent.relativeCoordinateToSelf(mainShipPos)))
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
