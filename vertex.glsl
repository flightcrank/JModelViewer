
#version 330 core

layout (location = 0) in vec3 vertex;
layout (location = 1) in vec3 normal;

uniform mat3 rotX;
uniform mat3 rotY;

out vec3 norm;
out vec3 vertPos;

void main() {
    
    mat3 rotObj = rotX * rotY;  //rotation matrix

    vec3 newVert = rotObj * vertex; //preform rotation on vertex
    vec3 newNorm = rotObj * normal; //preform rotation on normal

    vertPos = newVert;   //send vertex normal to fragment shader
    norm = newNorm;      //send vertex normal to fragment shader

    //plot final vertex position
    gl_Position = vec4(newVert, 1.0);
}
