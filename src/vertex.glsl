#version 330 core

layout (location = 0) in vec4 vertex;
layout (location = 1) in vec4 normal;

uniform vec3 offset;
uniform mat4 perspective;
uniform mat4 rotX;
uniform mat4 rotY;

out vec4 norm;
out vec4 vertPos;

void main() {
    
    vec4 cameraPos = vertex * rotY;
    vec4 n = normal * rotY;
    cameraPos = cameraPos + vec4(offset, 0.0);
    
    vec4 clipPos = perspective * cameraPos;
    
    vertPos = clipPos;
    norm = n;
    
    //plot final vertex position
    gl_Position = clipPos;
}
