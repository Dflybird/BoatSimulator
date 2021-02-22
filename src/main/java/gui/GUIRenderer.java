package gui;

import gui.graphic.Mesh;
import gui.graphic.Transformation;
import gui.graphic.light.PointLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.GUIState;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static conf.Constant.RESOURCES_SHADERS_DIR;

/**
 * 全局渲染，在此类中编辑环境渲染，实体渲染调用{@link GUIState}
 * @Author Gq
 * @Date 2021/1/31 13:06
 * @Version 1.0
 **/
public class GUIRenderer {
    private final Logger logger = LoggerFactory.getLogger(GUIRenderer.class);

    private Window window;
    private Scene scene;
    private GUIState guiState;
    private  Camera camera;

    private ShaderProgram sceneProgram;
    private ShaderProgram oceanProgram;

    private final float specularPower = 10f;
    private final Transformation transformation = new Transformation();

    public void init(Window window, Camera camera, Scene scene, GUIState guiState) {
        this.window = window;
        this.camera = camera;
        this.scene = scene;
        this.guiState = guiState;

        setupOceanShader();
        setupSceneShader();
    }

    public void render() {
        if (window.isResize(true)) {
            window.updateProjectionMatrix();
            window.updateViewPoint();
        }
        transformation.updateViewMatrix(camera);

        renderOcean();
        renderScene();
        renderHud();
    }

    private void setupOceanShader() {
        oceanProgram = new ShaderProgram();
        oceanProgram.init(new File(RESOURCES_SHADERS_DIR, "ocean.vert"),
                new File(RESOURCES_SHADERS_DIR, "ocean.frag"));
        oceanProgram.createUniform("world");
        oceanProgram.createUniform("view");
        oceanProgram.createUniform("projection");
        oceanProgram.createUniform("ambientLight");
        oceanProgram.createUniform("specularPower");
        oceanProgram.createMaterialUniform("material");
        oceanProgram.createFogUniform("fog");
        oceanProgram.createPointLightsUniform("pointLights", 5);
    }

    private void setupSceneShader(){
        sceneProgram = new ShaderProgram();
        sceneProgram.init(new File(RESOURCES_SHADERS_DIR, "entity.vert"),
                new File(RESOURCES_SHADERS_DIR, "entity.frag"));

        sceneProgram.createUniform("world");
        sceneProgram.createUniform("projection");

        sceneProgram.createUniform("texture_sampler");
        sceneProgram.createUniform("specularPower");
        sceneProgram.createMaterialUniform("material");
        sceneProgram.createUniform("ambientLight");
        sceneProgram.createPointLightUniform("pointLight");
        sceneProgram.createDirectionalLightUniform("directionalLight");
    }

    private void renderOcean() {
        oceanProgram.bind();
        Matrix4f viewMatrix = transformation.viewMatrix();

        oceanProgram.setUniform("projection", window.getProjectionMatrix());
        //渲染光
        SceneLight sceneLight = scene.getSceneLight();
        oceanProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        oceanProgram.setUniform("specularPower", 10f);
        oceanProgram.setUniform("fog", scene.getFog());
//            program.setUniform("directionalLight", directionalLight);
//        oceanProgram.setUniform("pointLights", sceneLight.getPointLightList());
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            oceanProgram.setUniform("pointLights", currPointLight, i);
        }

        //更新海浪mesh
        Map<Mesh, List<GameObj>> oceanMap = scene.getOceanMap();
        for (Mesh mesh : oceanMap.keySet()) {
            mesh.updateModel();
            oceanProgram.setUniform("material", mesh.getMaterial());
            List<GameObj> objList = oceanMap.get(mesh);
            mesh.render(objList,
                    obj -> oceanProgram.setUniform("world", transformation.worldMatrix(obj, viewMatrix)));
        }

        oceanProgram.unbind();
    }

    private void renderScene() {
        sceneProgram.bind();
        sceneProgram.setUniform("projection", window.getProjectionMatrix());
        //渲染光
        SceneLight sceneLight = scene.getSceneLight();
        sceneProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneProgram.setUniform("specularPower", specularPower);
        sceneProgram.setUniform("pointLight", new PointLight[]{});



        //渲染对象实体
        renderMeshes();

        sceneProgram.unbind();
    }

    private void renderMeshes() {
        Matrix4f viewMatrix = transformation.viewMatrix();

        sceneProgram.setUniform("texture_sampler", 0);
        Map<Mesh, List<GameObj>> meshMap = scene.getObjMesh();
        for (Mesh mesh : meshMap.keySet()) {
            sceneProgram.setUniform("material", mesh.getMaterial());
            List<GameObj> objList = meshMap.get(mesh);
            //更新obj姿态
            guiState.updateRenderState(objList);

            mesh.render(objList,
                    obj -> sceneProgram.setUniform("world", transformation.worldMatrix(obj, viewMatrix)));
        }
    }

    private void renderHud(){

    }

    public void cleanup() {
        if (sceneProgram != null) {
            sceneProgram.cleanup();
        }
    }

    public ShaderProgram getSceneProgram() {
        return sceneProgram;
    }


}
