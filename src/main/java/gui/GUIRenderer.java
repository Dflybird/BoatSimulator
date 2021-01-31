package gui;

import gui.graphic.Mesh;
import gui.graphic.Transformation;
import gui.graphic.light.DirectionalLight;
import gui.graphic.light.PointLight;
import gui.obj.Camera;
import gui.obj.GameObj;
import gui.shader.ShaderProgram;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.util.List;
import java.util.Map;

import static conf.Constant.RESOURCES_SHADERS_DIR;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * 全局渲染，在此类中编辑环境渲染，实体渲染调用{@link GUIState}
 * @Author Gq
 * @Date 2021/1/31 13:06
 * @Version 1.0
 **/
public class GUIRenderer {

    private Window window;
    private Scene scene;
    private GUIState guiState;
    private  Camera camera;

    private ShaderProgram sceneProgram;

    private final float specularPower = 10f;
    private final Transformation transformation = new Transformation();

    public void init(Window window, Camera camera, Scene scene, GUIState guiState) {
        this.window = window;
        this.camera = camera;
        this.scene = scene;
        this.guiState = guiState;
        setupSceneShader();
    }

    public void render() {
        if (window.isResize(true)) {
            window.updateProjectionMatrix();
            window.updateViewPoint();
        }
        transformation.updateViewMatrix(camera);

        renderScene(scene, guiState);
        renderHud();
    }

    private void setupSceneShader(){
        sceneProgram = new ShaderProgram();
        sceneProgram.init(new File(RESOURCES_SHADERS_DIR, "entity.vert"),
                new File(RESOURCES_SHADERS_DIR, "entity.frag"));

        sceneProgram.createUniform("world");
        sceneProgram.createUniform("projection");

        sceneProgram.createUniform("texture_sampler");
        sceneProgram.createUniform("specularPower");
        sceneProgram.createMaterialUniforms("material");
        sceneProgram.createUniform("ambientLight");
        sceneProgram.createPointLightUniforms("pointLight");
        sceneProgram.createDirectionalLightUniform("directionalLight");
    }

    PointLight.Attenuation att = new PointLight.Attenuation(0.6f, 0.2f, 0.2f);
    PointLight pointLight = new PointLight(new Vector3f(1, 1, 1), new Vector3f(0, 0, 3), 1.0f, att);
    DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), new Vector3f(-1, 0, 0), 1.0f);
    private void renderScene(Scene scene, GUIState guiState) {
        sceneProgram.bind();
        sceneProgram.setUniform("projection", window.getProjectionMatrix());
        //渲染光
        SceneLight sceneLight = scene.getSceneLight();
        sceneProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneProgram.setUniform("specularPower", specularPower);
        //TODO 渲染多个点光源
        PointLight currentPointLight = new PointLight(pointLight);
        Vector3f lightPos = currentPointLight.getPosition();
        Vector4f lightPosInWord = new Vector4f(lightPos, 1);
        lightPosInWord.mul(transformation.viewMatrix());
        lightPos.x = lightPosInWord.x;
        lightPos.y = lightPosInWord.y;
        lightPos.z = lightPosInWord.z;
        sceneProgram.setUniform("pointLight", currentPointLight);

        DirectionalLight currentDirectionalLight = new DirectionalLight(directionalLight);
        Vector3f lightDirection = currentDirectionalLight.getDirection();
        Vector4f lightDirectionInWord = new Vector4f(lightDirection, 0);
        lightDirectionInWord.mul(transformation.viewMatrix());
        lightDirection.x = lightDirectionInWord.x;
        lightDirection.y = lightDirectionInWord.y;
        lightDirection.z = lightDirectionInWord.z;
        sceneProgram.setUniform("directionalLight", currentDirectionalLight);



        //渲染对象实体
        renderMeshes(scene, guiState);

        sceneProgram.unbind();
    }

    private void renderMeshes(Scene scene, GUIState guiState) {
        //TODO 根据guiState计算obj当前状态


        Matrix4f viewMatrix = transformation.viewMatrix();

        sceneProgram.setUniform("texture_sampler", 0);
        Map<Mesh, List<GameObj>> meshMap = scene.getAllMesh();
        for (Mesh mesh : meshMap.keySet()) {
            sceneProgram.setUniform("material", mesh.getMaterial());
            mesh.render(meshMap.get(mesh),
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
