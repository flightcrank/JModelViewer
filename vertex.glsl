
#version 330 core

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec3 normal;

uniform mat3 rotX;
uniform mat3 rotY;

void main() {
    
    mat3 rotObj = rotX * rotY;
    vec3 res = rotObj * vertex;
    gl_Position = vec4(res.x, res.y, res.z, 1.0);
    

    //defualt vertex position
    //gl_Position = vec4(vertex, 1.0);
}
