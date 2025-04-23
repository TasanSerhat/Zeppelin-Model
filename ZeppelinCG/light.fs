#version 330

out vec4 outColor;
in vec2 texCoord;

uniform sampler2D text_samp;

void main()
{
    outColor = texture(text_samp, texCoord);
}