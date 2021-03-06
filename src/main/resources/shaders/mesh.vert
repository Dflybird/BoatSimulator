#version 330

layout (location =0) in vec3 vertexPosition;
layout (location =1) in vec2 textureCoordinate;
layout (location =2) in vec3 vertexNormal;

uniform mat4 world;
uniform mat4 projection;

void main()
{
    vec4 worldPos = world * vec4(vertexPosition, 1.0);
    gl_Position = projection * worldPos;
}