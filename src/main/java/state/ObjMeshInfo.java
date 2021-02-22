package state;

import java.util.Arrays;

/**
 * @Author Gq
 * @Date 2021/2/21 20:08
 * @Version 1.0
 **/
public class ObjMeshInfo {
    private int[] indices;
    private float[] vertices;
    private float[] textures;
    private float[] normals;

    public ObjMeshInfo() {
        indices = new int[0];
        vertices = new float[0];
        textures = new float[0];
        normals = new float[0];
    }

    public ObjMeshInfo(int iN, int vN, int nN, int tN) {
        indices = new int[iN];
        vertices = new float[vN];
        textures = new float[tN];
        normals = new float[nN];
    }

    public ObjMeshInfo(ObjMeshInfo objMeshInfo) {
        this.indices = objMeshInfo.indices;
        this.vertices = objMeshInfo.vertices;
        this.textures = objMeshInfo.textures;
        this.normals = objMeshInfo.normals;
    }

    public ObjMeshInfo mul(double num) {
        for (int i = 0; i < indices.length; i++) {
            indices[i] *= num;
        }
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] *= num;
        }
        for (int i = 0; i < textures.length; i++) {
            textures[i] *= num;
        }
        for (int i = 0; i < normals.length; i++) {
            normals[i] *= num;
        }
        return this;
    }

    public ObjMeshInfo add(ObjMeshInfo info) {
        for (int i = 0; i < indices.length; i++) {
            indices[i] += info.indices[i];
        }
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] += info.vertices[i];
        }
        for (int i = 0; i < textures.length; i++) {
            textures[i] += info.textures[i];
        }
        for (int i = 0; i < normals.length; i++) {
            normals[i] += info.normals[i];
        }
        return this;
    }

    public ObjMeshInfo sub(ObjMeshInfo info) {
        for (int i = 0; i < indices.length; i++) {
            indices[i] -= info.indices[i];
        }
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] -= info.vertices[i];
        }
        for (int i = 0; i < textures.length; i++) {
            textures[i] -= info.textures[i];
        }
        for (int i = 0; i < normals.length; i++) {
            normals[i] -= info.normals[i];
        }
        return this;
    }

    public int[] getIndices() {
        return indices;
    }

    public void setIndices(int[] indices) {
        this.indices = indices;
    }

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public float[] getTextures() {
        return textures;
    }

    public void setTextures(float[] textures) {
        this.textures = textures;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjMeshInfo that = (ObjMeshInfo) o;
        return Arrays.equals(indices, that.indices) && Arrays.equals(vertices, that.vertices) && Arrays.equals(textures, that.textures) && Arrays.equals(normals, that.normals);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(indices);
        result = 31 * result + Arrays.hashCode(vertices);
        result = 31 * result + Arrays.hashCode(textures);
        result = 31 * result + Arrays.hashCode(normals);
        return result;
    }
}
