#version 330

layout (location =0) in vec3 vertexPosition;
layout (location =1) in vec2 textureCoordinate;
layout (location =2) in vec3 vertexNormal;

out vec2 exTextureCoordinate;
out vec3 exWorldPos;
out vec3 exVertexNormal;

uniform mat4 world;
uniform mat4 projection;

void main()
{
    //将模型坐标系中的点坐标和法向量转换到世界坐标系
    vec4 worldPos = world * vec4(vertexPosition, 1.0);
    vec4 worldNor = world * vec4(vertexNormal, 0.0);
    gl_Position = projection * worldPos;
    exTextureCoordinate = textureCoordinate;
    exWorldPos = worldPos.xyz;
    exVertexNormal = normalize(worldNor).xyz;
}