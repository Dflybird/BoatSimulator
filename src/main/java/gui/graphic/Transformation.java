package gui.graphic;

import gui.obj.Camera;
import gui.obj.GameObj;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

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
        Vector3f rotation = obj.getRotation();
        float scale = obj.getScale();
        return modelMatrix.identity()
                .scale(scale)
                .translate(translation)
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z));
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
