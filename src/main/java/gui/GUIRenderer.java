package gui;

import gui.graphic.Material;
import gui.graphic.Mesh;
import gui.graphic.Transformation;
import gui.graphic.light.DirectionalLight;
import gui.graphic.light.PointLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.obj.Model;
import gui.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
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
    private ShaderProgram meshProgram;

    private final float specularPower = 10f;
    private final Transformation transformation = new Transformation();

    public void init(Window window, Camera camera, Scene scene, GUIState guiState) {
        this.window = window;
        this.camera = camera;
        this.scene = scene;
        this.guiState = guiState;

        setupOceanShader();
        setupSceneShader();
        setupMeshShader();
    }

    public void render() {
        if (window.isResize(true)) {
            window.updateProjectionMatrix();
            window.updateViewPoint();
        }
        transformation.updateViewMatrix(camera);

        renderScene();
        renderOcean();
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
        oceanProgram.createDirectionalLightUniform("directionalLight");
        oceanProgram.createPointLightsUniform("pointLights", 5);
        oceanProgram.createFogUniform("fog");
    }

    private void setupSceneShader(){
        sceneProgram = new ShaderProgram();
        sceneProgram.init(new File(RESOURCES_SHADERS_DIR, "entity.vert"),
                new File(RESOURCES_SHADERS_DIR, "entity.frag"));

        sceneProgram.createUniform("world");
        sceneProgram.createUniform("projection");

        sceneProgram.createUniform("texture_sampler");
        sceneProgram.createUniform("ambientLight");
        sceneProgram.createUniform("specularPower");
        sceneProgram.createMaterialUniform("material");
        sceneProgram.createDirectionalLightUniform("directionalLight");
        sceneProgram.createPointLightsUniform("pointLights", 5);
        sceneProgram.createSpotLightsUniform("spotLights", 5);
        sceneProgram.createFogUniform("fog");
    }

    private void setupMeshShader() {
        meshProgram = new ShaderProgram();
        meshProgram.init(new File(RESOURCES_SHADERS_DIR, "mesh.vert"),
                new File(RESOURCES_SHADERS_DIR, "mesh.frag"));
        meshProgram.createUniform("world");
        meshProgram.createUniform("projection");
    }

    private void renderOcean() {
        oceanProgram.bind();
        Matrix4f viewMatrix = transformation.viewMatrix();

        oceanProgram.setUniform("projection", window.getProjectionMatrix());
        //渲染光
        SceneLight sceneLight = scene.getSceneLight();
        oceanProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        oceanProgram.setUniform("specularPower", specularPower);
        oceanProgram.setUniform("fog", scene.getFog());
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
        DirectionalLight currentDirectionalLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector3f lightDirection = currentDirectionalLight.getDirection();
        Vector4f lightDirectionInWord = new Vector4f(lightDirection, 0);
        lightDirectionInWord.mul(viewMatrix);
        lightDirection.x = lightDirectionInWord.x;
        lightDirection.y = lightDirectionInWord.y;
        lightDirection.z = lightDirectionInWord.z;
        oceanProgram.setUniform("directionalLight", currentDirectionalLight);

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

        Matrix4f viewMatrix = transformation.viewMatrix();
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
            sceneProgram.setUniform("pointLights", currPointLight, i);
        }

        DirectionalLight currentDirectionalLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector3f lightDirection = currentDirectionalLight.getDirection();
        Vector4f lightDirectionInWord = new Vector4f(lightDirection, 0);
        lightDirectionInWord.mul(viewMatrix);
        lightDirection.x = lightDirectionInWord.x;
        lightDirection.y = lightDirectionInWord.y;
        lightDirection.z = lightDirectionInWord.z;
        sceneProgram.setUniform("directionalLight", currentDirectionalLight);

        sceneProgram.setUniform("fog", scene.getFog());

        //渲染对象实体
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

        sceneProgram.unbind();
    }

    public void renderMeshes(Model model, Vector3f translation, Quaternionf rotation, Vector3f scale) {
        meshProgram.bind();
        meshProgram.setUniform("projection", window.getProjectionMatrix());
        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.translate(translation)
                .rotate(rotation)
                .scale(scale);
        Matrix4f viewMatrix = transformation.viewMatrix();
        meshProgram.setUniform("world", transformation.worldMatrix(modelMatrix, viewMatrix));
        Mesh mesh = new Mesh(model, new Material());
        mesh.render();
        mesh.cleanup();
        meshProgram.unbind();
    }

    private void renderHud(){

    }

    public void cleanup() {
        if (sceneProgram != null) {
            sceneProgram.cleanup();
        }
        if (oceanProgram != null) {
            oceanProgram.cleanup();
        }
        if (meshProgram != null) {
            meshProgram.cleanup();
        }
    }

    public ShaderProgram getSceneProgram() {
        return sceneProgram;
    }


}
