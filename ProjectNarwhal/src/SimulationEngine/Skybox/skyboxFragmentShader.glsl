#version 150

in vec3 textureCoords;
out vec4 out_Colour;

uniform samplerCube cubeMap;

void main(void){
    out_Colour = texture(cubeMap, textureCoords) ;
}