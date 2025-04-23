#version 330

out vec2 texCoord;
out vec3 fragPos;  
out vec3 normals;
out vec3 lightPos;

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in vec3 aNormals;

uniform mat4 proj;
uniform mat4 model;
uniform mat4 camera;
uniform vec4 lPos;
uniform mat4 rot;

void main()
{
	gl_Position = proj * camera * rot * model * vec4(vertex_position, 1.0);
	fragPos = vec3(camera * rot * model * vec4(vertex_position, 1.0));
	normals = mat3(transpose(inverse(rot * model))) * aNormals;
	lightPos = vec3(camera * lPos);
	texCoord = aTexCoord;
}