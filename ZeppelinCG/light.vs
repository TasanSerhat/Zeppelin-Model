#version 330

out vec2 texCoord;

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec2 aTexCoord;

uniform mat4 proj;
uniform mat4 model;
uniform mat4 camera;
uniform mat4 rot;

void main()
{
    gl_Position = proj * camera * rot * model * vec4(vertex_position, 1.0);
	texCoord = aTexCoord;
}