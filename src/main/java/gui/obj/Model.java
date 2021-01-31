package gui.obj;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Gq
 * @Date 2020/12/20 20:57
 * @Version 1.0
 **/
public class Model {

    private int[] indices;
    private float[] vertices;
    private float[] textures;
    private float[] normals;

    public Model(int[] indices, float[] vertices, float[] textures, float[] normals) {
        this.indices = indices;
        this.vertices = vertices;
        this.textures = textures;
        this.normals = normals;
    }

    public static Model loadObj(File file) {
        String line;

        List<Vector3f> verticesList = new ArrayList<>();
        List<Vector2f> texturesList = new ArrayList<>();
        List<Vector3f> normalsList = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((line = bufferedReader.readLine()) != null) {
                String[] slices = line.split("\\s+");
                switch (slices[0]) {
                    case "v":
                        verticesList.add(new Vector3f(
                                Float.parseFloat(slices[1]),
                                Float.parseFloat(slices[2]),
                                Float.parseFloat(slices[3])));
                        break;
                    case "vt":
                        texturesList.add(new Vector2f(
                                Float.parseFloat(slices[1]),
                                Float.parseFloat(slices[2])));
                        break;
                    case "vn":
                        normalsList.add(new Vector3f(
                                Float.parseFloat(slices[1]),
                                Float.parseFloat(slices[2]),
                                Float.parseFloat(slices[3])));
                        break;
                    case "f":
                        faces.add(new Face(slices[1], slices[2], slices[3]));
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int vertexNum = verticesList.size();
        int[] indices = new int[faces.size() * 3];
        float[] vertices = new float[vertexNum * 3];
        float[] textures = new float[vertexNum * 2];
        float[] normals = new float[vertexNum * 3];

        int index = 0;
        for(Face face : faces) {
            for (Group group : face.getGroups()) {
                int vertexIndex = group.indexVertices;
                int textureIndex = group.indexTextures;
                int normalIndex = group.indexNormals;
                Vector3f vertex = verticesList.get(vertexIndex);
                vertices[vertexIndex * 3] = vertex.x;
                vertices[vertexIndex * 3 + 1] = vertex.y;
                vertices[vertexIndex * 3 + 2] = vertex.z;
                if (textureIndex != Group.NO_VALUE) {
                    Vector2f texture = texturesList.get(textureIndex);
                    textures[vertexIndex * 2] = texture.x;
                    textures[vertexIndex * 2 + 1] = 1 - texture.y;
                }
                if (normalIndex != Group.NO_VALUE) {
                    Vector3f normal = normalsList.get(normalIndex);
                    normals[vertexIndex * 3] = normal.x;
                    normals[vertexIndex * 3 + 1] = normal.y;
                    normals[vertexIndex * 3 + 2] = normal.z;
                }
                indices[index++] = vertexIndex;
            }
        }

        return new Model(indices, vertices, textures, normals);
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextures() {
        return textures;
    }

    public float[] getNormals() {
        return normals;
    }

    private static class Face{

        Group[] groups = new Group[3];

        public Face(String group0, String group1, String group2) {
            groups[0] = paresGroup(group0);
            groups[1] = paresGroup(group1);
            groups[2] = paresGroup(group2);
        }

        private Group paresGroup(String groupStr) {
            //索引组存在形式: v  v/t  v/t/f  v//f
            Group group = new Group();
            String[] slices = groupStr.split("/");
            //数组索引从0开始，obj中索引从1开始
            group.indexVertices = Integer.parseInt(slices[0]) - 1;

            if (slices.length > 1) {
                String slice = slices[1];
                if (slice.length() > 0) {
                    group.indexTextures = Integer.parseInt(slice) - 1;
                }
                if (slices.length > 2) {
                    group.indexNormals = Integer.parseInt(slices[2]) - 1;
                }
            }
            return group;
        }

        public Group[] getGroups() {
            return groups;
        }
    }

    private static class Group{
        public static final int NO_VALUE = -1;

        public int indexVertices = NO_VALUE;
        public int indexTextures = NO_VALUE;
        public int indexNormals = NO_VALUE;
    }
}
