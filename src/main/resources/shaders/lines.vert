#version 460

layout (location = 0) in vec3 vertex_position;
layout (location = 1) in vec3 vertex_normal;
layout (location = 2) in vec2 vertex_uv;

uniform mat3 normalMatrix;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec3 position;
out vec3 normal;

void main() {
  vec4 p = modelMatrix * vec4(vertex_position, 1.0);
  position = p.xyz;
  normal = normalMatrix * vertex_normal;
  gl_Position = projectionMatrix * viewMatrix * p;
}