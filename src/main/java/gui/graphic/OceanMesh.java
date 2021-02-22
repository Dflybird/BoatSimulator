//package gui.graphic;
//
//import gui.obj.GameObj;
//import org.joml.Vector4f;
//import org.lwjgl.system.MemoryUtil;
//import util.TimeUtil;
//
//import java.nio.FloatBuffer;
//import java.nio.IntBuffer;
//import java.util.List;
//import java.util.function.Consumer;
//
//import static org.lwjgl.opengl.GL15.*;
//import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
//import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
//import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
//import static org.lwjgl.opengl.GL30.glBindVertexArray;
//import static org.lwjgl.opengl.GL30.glGenVertexArrays;
//
///**
// * @Author Gq
// * @Date 2021/2/21 15:50
// * @Version 1.0
// **/
//public class OceanMesh {
//
//    private int vertexPosition = 0;
//    private int textureCoordinate = 1;
//    private int vertexNormal = 2;
//
//    //VAO
//    private int vaoId;
//
//    //VBOs
//    private int positionVboId;
//    private int indexVboId;
//    private int textureVboId;
//    private int normalVboId;
//
//
//    private FloatBuffer verticesBuffer;
//    private FloatBuffer normalsBuffer;
//    private IntBuffer indicesBuffer;
//
//    private Ocean ocean;
//    private Material material;
//    private final int vertexCount;
//
//    public OceanMesh(Ocean ocean) {
//        this.ocean = ocean;
//        this.material = new Material(
//                new Vector4f(0.0f, 0.65f, 0.75f, 1.0f),
//                new Vector4f(0.5f, 0.65f, 0.75f, 1.0f),
//                new Vector4f(1.0f, 0.25f, 0.0f,  1.0f),
//                1, null);;
//        this.vertexCount = ocean.getIndices().length;
//
//        vaoId = glGenVertexArrays();
//        glBindVertexArray(vaoId);
//
//        initIndexBuffer(ocean.getIndices());
//        initPositionBuffer(ocean.getVertices());
//        initNormalBuffer(ocean.getNormals());
//
//        //解绑VAO
//        glBindVertexArray(0);
//    }
//
//
//
//    public void render(List<GameObj> objects, Consumer<GameObj> consumer) {
//        glBindVertexArray(vaoId);
//
//        verticesBuffer.clear();
//        verticesBuffer.put(ocean.getVertices()).flip();
//        glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
//        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
//        glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
//        glEnableVertexAttribArray(vertexPosition);
//
//        normalsBuffer.clear();
//        normalsBuffer.put(ocean.getNormals()).flip();
//        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
//        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
//        glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
//        glEnableVertexAttribArray(vertexNormal);
//
//        //解绑VBOs
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//
//        //平铺
//        for (GameObj obj : objects) {
//            //计算坐标位置信息
//            System.out.println(obj.getTranslation());
//            consumer.accept(obj);
//            glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
//        }
//
//        glBindVertexArray(0);
//    }
//
//    private void initPositionBuffer(float[] positions){
//        verticesBuffer = MemoryUtil.memAllocFloat(positions.length);
//        verticesBuffer.put(positions).flip();
//        positionVboId = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, positionVboId);
//        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
//        glVertexAttribPointer(vertexPosition, 3, GL_FLOAT, false, 0, 0);
//        glEnableVertexAttribArray(vertexPosition);
//        //解绑VBOs
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//    }
//
//    private void initIndexBuffer(int[] indices){
//        indicesBuffer = MemoryUtil.memAllocInt(indices.length);
//        indicesBuffer.put(indices).flip();
//        indexVboId = glGenBuffers();
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexVboId);
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
//    }
//
//    private void initNormalBuffer(float[] normals) {
//        normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
//        normalsBuffer.put(normals).flip();
//        normalVboId = glGenBuffers();
//        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
//        glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);
//        glVertexAttribPointer(vertexNormal, 3, GL_FLOAT, false, 0, 0);
//        glEnableVertexAttribArray(vertexNormal);
//        //解绑VBOs
//        glBindBuffer(GL_ARRAY_BUFFER, 0);
//    }
//
//    public void cleanup(){
//        //释放缓存
//        if (verticesBuffer != null) {
//            MemoryUtil.memFree(verticesBuffer);
//        }
//        if (normalsBuffer != null) {
//            MemoryUtil.memFree(normalsBuffer);
//        }
//        if (indicesBuffer != null) {
//            MemoryUtil.memFree(indicesBuffer);
//        }
//    }
//
//    public Material getMaterial() {
//        return material;
//    }
//}
