package state;

import ams.StateUpdateListener;
import conf.Constant;
import gui.SyncedCacheBuffer;
import gui.graphic.Mesh;
import gui.obj.GameObj;
import gui.obj.Model;
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

        //不改变缓存 | guiState = (currentSimState - previousSimState) * alpha + previousSimState;
        renderState = renderState.zero().add(currentState).sub(previousState).mul(alpha).add(previousState);
    }

    @Override
    public void stateInit(SimState simState) {
        this.simStateBuffer = new SyncedCacheBuffer<>(simState);
        this.renderState = new SimState(simState);
    }

    @Override
    public void stateUpdated(SimState simState) {
        simStateBuffer.update(simState);
    }

    public void updateRenderState(List<GameObj> objList) {
        for (GameObj obj : objList) {
            ObjStateInfo objStateInfo = renderState.getStateInfo(obj.getID());
            if (objStateInfo != null) {
                float[] t = objStateInfo.getTranslation();
                float[] r = objStateInfo.getRotation();
                float s = objStateInfo.getScale();
                obj.setTranslation(new Vector3f(t[0], t[1], t[2]));
                obj.setRotation(new Vector3f(r[0], r[1], r[2]));
                obj.setScale(s);
            }
        }
    }
}
