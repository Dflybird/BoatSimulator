package gui;

import gui.graphic.light.*;
import org.joml.Vector3f;

/**
 * 环境光源
 * @Author Gq
 * @Date 2021/1/31 13:09
 * @Version 1.0
 **/
public class SceneLight {

    private Vector3f ambientLight;

    private PointLight[] pointLightList;

    private DirectionalLight directionalLight;

    private SpotLight[] spotLightList;

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public PointLight[] getPointLightList() {
        return pointLightList;
    }

    public void setPointLightList(PointLight[] pointLightList) {
        this.pointLightList = pointLightList;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public SpotLight[] getSpotLightList() {
        return spotLightList;
    }

    public void setSpotLightList(SpotLight[] spotLightList) {
        this.spotLightList = spotLightList;
    }
}
