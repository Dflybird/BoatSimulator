package gui;

import ams.SimState;
import ams.StateUpdateListener;
import gui.obj.GameObj;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 存储并渲染实体对象
 * @Author: gq
 * @Date: 2021/1/22 13:02
 */
public class GUIState implements StateUpdateListener {

    private final Logger logger = LoggerFactory.getLogger(GUIState.class);

    private SyncedCacheBuffer<SimState> simStateBuffer;
    private SimState renderState;

    public void computeRenderState(double alpha) {
        List<SimState> bufferList = simStateBuffer.getLatest();
        SimState currentState = bufferList.get(0);
        SimState previousState = bufferList.get(1);

        //不改变缓存
        //guiState = (currentSimState - previousSimState) * alpha + previousSimState;
        renderState = renderState.zero().add(currentState).sub(previousState).mul(alpha).add(previousState);
        float[] pre = renderState.getState("test1").getTranslation();
        float[] cur = currentState.getState("test1").getTranslation();
        logger.debug("pre.p: {}, {}, {} | cur.p: {}, {}, {}", pre[0], pre[1], pre[2], cur[0], cur[1], cur[2]);
    }

    @Override
    public void stateInit(SimState simState) {
        this.simStateBuffer = new SyncedCacheBuffer<>(simState);
        this.renderState = new SimState(simState);
    }

    @Override
    public void stateUpdated(SimState simState) {
//        if ( renderState.getState("test1") != null) {
//            float[] pre = renderState.getState("test1").getTranslation();
//            float[] cur = simState.getState("test1").getTranslation();
//            logger.debug("pre.p: {}, {}, {} | cur.p: {}, {}, {}", pre[0], pre[1], pre[2], cur[0], cur[1], cur[2]);
//        } else {
//            logger.debug("nul");
//        }
        simStateBuffer.update(simState);
    }

    public void updateRenderState(List<GameObj> objList) {
        for (GameObj obj : objList) {
            SimState.StateInfo stateInfo = renderState.getState(obj.getID());
            if (stateInfo != null) {
                float[] t = stateInfo.getTranslation();
                float[] r = stateInfo.getRotation();
                float s = stateInfo.getScale();
                obj.setTranslation(new Vector3f(t[0], t[1], t[2]));
                obj.setRotation(new Vector3f(r[0], r[1], r[2]));
                obj.setScale(s);
            }
        }
    }
}
