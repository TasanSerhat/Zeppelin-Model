#version 330

out vec4 outColor;

in vec2 texCoord;
in vec3 fragPos;
in vec3 normals;
in vec3 lightPos;

uniform sampler2D text_samp;

vec3 lightColor = vec3(1.0, 1.0, 1.0);

void main()
{
	// ambient
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * lightColor;    
    
     // diffuse 
    vec3 norm = normalize(normals);
    vec3 lightDir = normalize(lightPos - fragPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;
    
    // specular
    float specularStrength = 0.5;
    vec3 viewDir = normalize(-fragPos); // the viewer is always at (0,0,0) in view-space, so viewDir is (0,0,0) - Position => -Position
    vec3 reflectDir = reflect(-lightDir, norm);  
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = specularStrength * spec * lightColor; 

	vec3 result = (ambient + specular + diffuse) * vec3(texture(text_samp, texCoord));
	outColor = vec4(result, 1.0);
}