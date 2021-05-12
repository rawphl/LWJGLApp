#version 460

uniform samplerCube cubeMap;
uniform mat3 normalMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 cameraPosition;

in vec3 position;
in vec3 normal;
in vec2 uv;

out vec4 fragColor;

void main() {
  fragColor = texture(cubeMap, position);
  //fragColor = vec4(position, 1.0);
}