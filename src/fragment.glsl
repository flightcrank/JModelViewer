#version 330 core

out vec4 FragColor;

uniform vec3 panelResolution;
uniform vec3 modelColour;
uniform vec3 lightPosition;
uniform sampler2D ourTexture;

in vec4 norm;
in vec4 vertPos;
in vec2 uv;

void main() {
    
    float ambiantStrength = 0.2;
    vec3 lightColour = vec3(1.0, 1.0, 1.0);

    vec3 ambient = ambiantStrength * lightColour;

    vec3 n = normalize(norm.xyz);
    vec3 lightDir = normalize(lightPosition - vertPos.xyz);

    float diff = max(dot(n, lightDir), 0.0);
    vec3 diffuse = diff * lightColour;  

    vec3 result = (ambient + diffuse) * modelColour;
    
    //FragColor = vec4(result, 1.0f);
    vec4 lightCol = vec4(result, 1.0f);
    FragColor = lightCol * texture(ourTexture, uv);
    
    
} 