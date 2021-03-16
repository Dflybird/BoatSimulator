package gui.graphic;

import com.sun.org.apache.xpath.internal.operations.Mod;
import gui.obj.GameObj;
import gui.obj.Model;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Consumer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @Author Gq
 * @Date 2020/12/17 21:07
 * @Version 1.0
 **/
public class Mesh {
    private final Logger logger = LoggerFactory.getLogger(Mesh.class);

    private final Model model;
    private final int vertexPosition = 0;
    private final int textureCoordinate = 1;
    private final int vertexNormal = 2;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int textureVboId;
    private int normalVboId;

    //VAO
    private final int vaoId;

    private int vertexCount;

    private Material material;

    private FloatBuffer verticesBuffer;
    private FloatBuffer normalsBuffer;
    private IntBuffer indicesBuffer;
    private FloatBuffer textureBuffer;

    public Mesh(Model model) {
        this.model = model;
        this.vertexCount = model.getIndices().length;

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        initPositionBuffer(model.getVertices());
        initIndexBuffer(model.getIndices());
        initTextureBuffer(model.getTextures());
        initNormalBuffer(model.getNormals());

        //解绑VAO
        glBindVertexArray(0);
    }

    public Mesh(Model model, Material material){
        this(model);
        this.material = material;
    }

    public void updateModel() {

        indicesBuffer.clear();
        indicesBuffer.put(model.getIndices()).flip();
        indexVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

        verticesBuffer.clear();
        verticesBuffer.put(model.getVertices()).flip();
        glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(vertexPosition);

        normalsBuffer.clear();
        normalsBuffer.put(model.getNormals()).flip();
        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(vertexNormal);

        textureBuffer.clear();
        textureBuffer.put(model.getTextures()).flip();
        textureVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(textureCoordinate, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(textureCoordinate);

        //解绑VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void render() {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
        }
        glBindVertexArray(vaoId);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
    }

    public void render(List<GameObj> objects, Consumer<GameObj> consumer) {
        Texture texture = material.getTexture();
        if (texture != null) {
            // Activate firs texture bank
            glActiveTexture(GL_TEXTURE0);
            // Bind the texture
            glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
        }

        glBindVertexArray(vaoId);

        for (GameObj obj : objects) {
            if (obj.isRender()) {
                //计算坐标位置信息
                consumer.accept(obj);
                glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
            }
        }

        glBindVertexArray(0);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(vertexPosition);
        glDisableVertexAttribArray(textureCoordinate);
        glDisableVertexAttribArray(vertexNormal);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(positionVboId);
        glDeleteBuffers(indexVboId);
        glDeleteBuffers(textureVboId);
        glDeleteBuffers(normalVboId);

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        glBindTexture(GL_TEXTURE_2D, 0);

        Texture texture = material.getTexture();
        if (texture != null) {
            material.getTexture().cleanup();
        }

        //释放内存
        if (verticesBuffer != null) {
            MemoryUtil.memFree(verticesBuffer);
        }
        if (normalsBuffer != null) {
            MemoryUtil.memFree(normalsBuffer);
        }
        if (indicesBuffer != null) {
            MemoryUtil.memFree(indicesBuffer);
        }
    }

    private void initIndexBuffer(int[] indices){
        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        indicesBuffer.put(indices).flip();
        indexVboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
    }

    private void initPositionBuffer(float[] positions){
        verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
        verticesBuffer.put(positions).flip();
        positionVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(vertexPosition);
        //解绑VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void initTextureBuffer(float[] textureCoordinates) {
        textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
        textureBuffer.put(textureCoordinates).flip();
        textureVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
        glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(textureCoordinate, 2, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(textureCoordinate);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private void initNormalBuffer(float[] normals) {
        normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalsBuffer.put(normals).flip();
        normalVboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(vertexNormal);
        //解绑VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Model getModel() {
        return model;
    }
}
