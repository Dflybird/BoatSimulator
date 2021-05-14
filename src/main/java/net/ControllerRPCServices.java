package net;

import ams.AgentManager;
import ams.agent.Agent;
import ams.agent.OnDoneAgent;
import ams.agent.usv.USVAgent;
import ams.msg.SteerMessage;
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

import static conf.Constant.ON_DONE_AGENT;

/**
 * @Author: gq
 * @Date: 2021/5/10 19:22
 */
public class ControllerRPCServices extends ControllerGrpc.ControllerImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ControllerRPCServices.class);

    private final GameLogic gameLogic;

    private int sizeStep = 0;
    private int allySize = 0;
    private int enemySize = 0;

    public ControllerRPCServices(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    @Override
    public void getObservation(Null request, StreamObserver<Information> responseObserver) {
        List<TeamInfo> teamInfo = new ArrayList<>();
        teamInfo.add(getAllyObservation());
        teamInfo.add(getEnemyObservation());

        Information information = Information.newBuilder()
                .addAllTeamInfo(teamInfo).build();
        responseObserver.onNext(information);
        responseObserver.onCompleted();
    }

    @Override
    public void step(Command request, StreamObserver<Fitness> responseObserver) {
        HashMap<USVAgent.Camp, Float> rewardMap = new HashMap<>();
        for (TeamCommand teamCommand : request.getTeamCommandList()) {
            USVAgent.Camp camp = USVAgent.Camp.campOf(teamCommand.getCamp());
            rewardMap.put(camp, 0f);

            for (MemberCommand memberCommand : teamCommand.getMemberCommandList()) {
                String agentID = memberCommand.getId();
                int steerType = memberCommand.getSteerType();
                int throttleType = memberCommand.getThrottleType();
                SteerMessage steerMessage = new SteerMessage(steerType, throttleType);
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

            List<TeamFitness> list = new ArrayList<>(rewardMap.size());
            for (Map.Entry<USVAgent.Camp, Float> entry : rewardMap.entrySet()) {
                TeamFitness teamFitness = TeamFitness.newBuilder()
                        .setCamp(entry.getKey().toInteger())
                        .setFitness(entry.getValue())
                        .build();
                list.add(teamFitness);
                logger.debug("camp: {} | fitness: {}", entry.getKey(), entry.getValue());
            }

            OnDoneAgent onDoneAgent = (OnDoneAgent) AgentManager.getAgent(ON_DONE_AGENT);

            Fitness fitness = Fitness.newBuilder()
                    .setDone(onDoneAgent.isDone())
                    .setTimeStep(sizeStep)
                    .addAllTeamFitness(list)
                    .build();

            responseObserver.onNext(fitness);
            responseObserver.onCompleted();
            logger.info("ally: {} | enemy: {} | step: {}", allySize, enemySize, sizeStep++);
        });
    }

    @Override
    public void reset(Null request, StreamObserver<Null> responseObserver) {
        gameLogic.reset();
        responseObserver.onNext(Null.newBuilder().build());
        responseObserver.onCompleted();
        sizeStep=0;
        allySize=0;
        enemySize=0;
    }

    private TeamInfo getAllyObservation() {
        allySize=0;
        List<VesselInfo> vessels = new ArrayList<>();
        String mainShipId = AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, SceneConfig.loadConfig().getMainShip().getId());
        USVAgent mainShip = (USVAgent) AgentManager.getAgent(mainShipId);
        TargetInfo targetInfo = TargetInfo.newBuilder()
                .setId(mainShipId)
                .setPosition(newVector3(mainShip.getEntity().getTranslation()))
                .setForward(newVector3(mainShip.getCurrForward()))
                .build();

        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (agent instanceof USVAgent) {
                USVAgent usvAgent = (USVAgent) agent;
                if (usvAgent.getCamp() == USVAgent.Camp.ALLY && usvAgent.getStatus() == USVAgent.Status.ALIVE) {
                    allySize++;

                    InternalState internalState = InternalState.newBuilder()
                            .setRadarRange(usvAgent.getDetector().getDetectRange())
                            .setWeaponRange(usvAgent.getWeapon().getAttackRange())
                            .setWeaponAngle(usvAgent.getWeapon().getAttackAngle())
                            .setPower(usvAgent.getEngine().getCurrentEnginePower())
                            .build();

                    ExternalState externalState = ExternalState.newBuilder()
                            .setPosition(newVector3(usvAgent.getEntity().getTranslation()))
                            .setForward(newVector3(usvAgent.getCurrForward()))
                            .setSpeed(usvAgent.getEntity().getLinearVelocity().length())
                            .build();

                    List<VesselsInRange> vesselsInRanges = new ArrayList<>();

                    for (USVAgent vessel : usvAgent.getDetector().usvInRange()) {
                        VesselsInRange vesselsInRange;
                        if (vessel.getCamp() == USVAgent.Camp.ALLY) {
                            vesselsInRange = VesselsInRange.newBuilder()
                                    .setId(vessel.getAgentID())
                                    .setCamp(0)
                                    .setPosition(newVector3(vessel.getEntity().getTranslation()))
                                    .setForward(newVector3(vessel.getCurrForward()))
                                    .setSpeed(vessel.getEntity().getLinearVelocity().length())
                                    .build();
                        } else if (vessel.getCamp() == USVAgent.Camp.ENEMY) {
                            vesselsInRange = VesselsInRange.newBuilder()
                                    .setId(vessel.getAgentID())
                                    .setCamp(1)
                                    .setPosition(newVector3(vessel.getEntity().getTranslation()))
                                    .setForward(newVector3(vessel.getCurrForward()))
                                    .setSpeed(vessel.getEntity().getLinearVelocity().length())
                                    .build();
                        } else {
                            continue;
                        }
                        vesselsInRanges.add(vesselsInRange);
                    }

                    ObservedState observedState = ObservedState.newBuilder()
                            .addAllVessels(vesselsInRanges)
                            .setTarget(targetInfo)
                            .build();

                    VesselInfo vesselInfo = VesselInfo.newBuilder()
                            .setId(usvAgent.getAgentID())
                            .setInternalState(internalState)
                            .setExternalState(externalState)
                            .setObservedState(observedState)
                            .build();

                    vessels.add(vesselInfo);
                }
            }
        }

        return TeamInfo.newBuilder()
                .setCamp(0)
                .addAllVesselInfo(vessels)
                .build();
    }

    private TeamInfo getEnemyObservation() {
        enemySize=0;
        List<VesselInfo> vessels = new ArrayList<>();
        String mainShipId = AgentUtil.assembleName(USVAgent.Camp.MAIN_SHIP, SceneConfig.loadConfig().getMainShip().getId());
        USVAgent mainShip = (USVAgent) AgentManager.getAgent(mainShipId);
        TargetInfo targetInfo = TargetInfo.newBuilder()
                .setId(mainShipId)
                .setPosition(newVector3(mainShip.getEntity().getTranslation()))
                .setForward(newVector3(mainShip.getCurrForward()))
                .build();

        for (Agent agent : AgentManager.getAgentMap().values()) {
            if (agent instanceof USVAgent) {
                USVAgent usvAgent = (USVAgent) agent;
                if (usvAgent.getCamp() == USVAgent.Camp.ENEMY && usvAgent.getStatus() == USVAgent.Status.ALIVE) {
                    enemySize++;

                    InternalState internalState = InternalState.newBuilder()
                            .setRadarRange(usvAgent.getDetector().getDetectRange())
                            .setWeaponRange(usvAgent.getWeapon().getAttackRange())
                            .setWeaponAngle(usvAgent.getWeapon().getAttackAngle())
                            .setPower(usvAgent.getEngine().getCurrentEnginePower())
                            .build();

                    ExternalState externalState = ExternalState.newBuilder()
                            .setPosition(newVector3(usvAgent.getEntity().getTranslation()))
                            .setForward(newVector3(usvAgent.getCurrForward()))
                            .setSpeed(usvAgent.getEntity().getLinearVelocity().length())
                            .build();

//                    logger.debug("for: {}", Math.atusvAgent.getCurrForward());

                    List<VesselsInRange> vesselsInRanges = new ArrayList<>();

                    for (USVAgent vessel : usvAgent.getDetector().usvInRange()) {
                        VesselsInRange vesselsInRange;
                        if (vessel.getCamp() == USVAgent.Camp.ALLY) {
                            vesselsInRange = VesselsInRange.newBuilder()
                                    .setId(vessel.getAgentID())
                                    .setCamp(0)
                                    .setPosition(newVector3(vessel.getEntity().getTranslation()))
                                    .setForward(newVector3(vessel.getCurrForward()))
                                    .setSpeed(vessel.getEntity().getLinearVelocity().length())
                                    .build();
                        } else if (vessel.getCamp() == USVAgent.Camp.ENEMY) {
                            vesselsInRange = VesselsInRange.newBuilder()
                                    .setId(vessel.getAgentID())
                                    .setCamp(1)
                                    .setPosition(newVector3(vessel.getEntity().getTranslation()))
                                    .setForward(newVector3(vessel.getCurrForward()))
                                    .setSpeed(vessel.getEntity().getLinearVelocity().length())
                                    .build();
                        } else {
                            continue;
                        }
                        vesselsInRanges.add(vesselsInRange);
                    }

                    ObservedState observedState = ObservedState.newBuilder()
                            .addAllVessels(vesselsInRanges)
                            .setTarget(targetInfo)
                            .build();

                    VesselInfo vesselInfo = VesselInfo.newBuilder()
                            .setId(usvAgent.getAgentID())
                            .setInternalState(internalState)
                            .setExternalState(externalState)
                            .setObservedState(observedState)
                            .build();

                    vessels.add(vesselInfo);
                }
            }
        }

        return TeamInfo.newBuilder()
                .setCamp(1)
                .addAllVesselInfo(vessels)
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
