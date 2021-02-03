package gui.graphic;

import gui.obj.GameObj;
import gui.obj.Model;
import org.lwjgl.system.MemoryUtil;

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
    private final Model model;

    private final int vertexPosition;
    private final int textureCoordinate;
    private final int vertexNormal;

    //VBOs
    private int positionVboId;
    private int indexVboId;
    private int textureVboId;
    private int normalVboId;

    //VAO
    private final int vaoId;

    private final int vertexCount;

    private Material material;

    public Mesh(Model model) {
        this.model = model;
        this.vertexPosition = 0;
        this.textureCoordinate = 1;
        this.vertexNormal = 2;
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
            //计算坐标位置信息
            consumer.accept(obj);
            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
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
    }

    private void initPositionBuffer(float[] positions){
        FloatBuffer verticesBuffer = null;
        try {
            verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
            verticesBuffer.put(positions).flip();
            positionVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
            glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(vertexPosition);
            //解绑VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            //释放缓存
            if (verticesBuffer != null) {
                MemoryUtil.memFree(verticesBuffer);
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    private void initIndexBuffer(int[] indices){
        IntBuffer indicesBuffer = null;
        try {
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            indexVboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        } finally {
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    private void initTextureBuffer(float[] textureCoordinates) {
        FloatBuffer textureBuffer = null;
        try {
            textureBuffer = MemoryUtil.memAllocFloat(textureCoordinates.length);
            textureBuffer.put(textureCoordinates).flip();
            textureVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(textureCoordinate, 2, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(textureCoordinate);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            if (textureBuffer != null) {
                MemoryUtil.memFree(textureBuffer);
            }
        }
    }

    private void initNormalBuffer(float[] normals) {
        FloatBuffer normalsBuffer = null;

        try {
            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalsBuffer.put(normals).flip();
            normalVboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
            glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(vertexNormal);
            //解绑VBOs
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        } finally {
            //释放缓存
            if (normalsBuffer != null) {
                MemoryUtil.memFree(normalsBuffer);
            }
        }
    }


}
