#version 330 core

layout (location = 0) in vec4 vertex;
layout (location = 1) in vec4 normal;
layout (location = 2) in vec2 tex;

uniform vec3 offset;
uniform mat4 perspective;
uniform mat4 rotX;
uniform mat4 rotY;

out vec4 norm;
out vec4 vertPos;
out vec2 uv;

void main() {
    
    vec4 cameraPos = vertex * rotY * rotX;
    vec4 n = normal * rotY * rotX;
    cameraPos = cameraPos + vec4(offset, 0.0);
    
    vec4 clipPos = perspective * cameraPos;
    
    vertPos = clipPos;
    norm = n;
    uv = tex;
    
    //plot final vertex position
    gl_Position = clipPos;
}
