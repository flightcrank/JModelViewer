
#version 330 core

out vec4 FragColor;

uniform vec3 modelColour;
uniform vec3 lightPosition;

in vec3 norm;
in vec3 vertPos;

void main() {
    
    float ambiantStrength = 0.15;
    vec3 lightColour = vec3(1.0, 1.0, 1.0);

    vec3 ambient = ambiantStrength * lightColour;
    vec3 colour = ambient * modelColour;

    vec3 n = normalize(norm);
    vec3 lightDir = normalize(lightPosition - vertPos);

    float diff = max(dot(n, lightDir), 0.0);
    vec3 diffuse = diff * lightColour;  

    vec3 result = (ambient + diffuse) * modelColour;

    FragColor = vec4(result, 1.0f);
} 