package gui.graphic;

import gui.obj.Camera;
import gui.obj.GameObj;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Transformation {

    private static final Logger logger = LoggerFactory.getLogger(Transformation.class);

    private final Matrix4f modelMatrix;
    
    private final Matrix4f worldMatrix;

    private final Matrix4f viewMatrix;


    public Transformation() {
        modelMatrix = new Matrix4f();
        worldMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }
    
    public Matrix4f modelMatrix(GameObj obj) {
        Vector3f translation = obj.getTranslation();
        Quaternionf rotation = obj.getRotation();
        Vector3f scale = obj.getScale();
        return modelMatrix.identity()
                .translate(translation)
                .rotate(rotation)
                .scale(scale);
    }

    public Matrix4f worldMatrix(GameObj obj, Matrix4f viewMatrix) {
        return worldMatrix(modelMatrix(obj), viewMatrix);
    }
    
    public Matrix4f worldMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {
        return viewMatrix.mulAffine(modelMatrix, worldMatrix);
    }

    public Matrix4f updateViewMatrix(Camera camera) {
        Vector3f cameraPos = camera.getPosition();
        Vector3f cameraRot = camera.getRotation();
        viewMatrix.identity()
                .rotateX((float) Math.toRadians(cameraRot.x))
                .rotateY((float) Math.toRadians(cameraRot.y))
                .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public Matrix4f viewMatrix() {
        return viewMatrix;
    }

}
