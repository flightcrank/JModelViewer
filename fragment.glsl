
#version 330 core

out vec4 FragColor;

uniform vec3 modelColour;

void main() {
    
    float ambiantStrength = 0.15;
    vec3 light = vec3(1.0, 1.0, 1.0);

    vec3 ambient = ambiantStrength * light;
    vec3 colour = ambient * modelColour;

    FragColor = vec4(colour, 1.0f);
} 