#version 460

in vec3 position;
in vec3 normal;

out vec4 fragColour;

const vec3 lightPosition = vec3(1.0, 1.0, 1.0);

void main() {
  vec3 light = normalize(-lightPosition - position);
  float ndl = max(dot(normal, light), 0);
  fragColour = vec4(vec3(0.5) * vec3(1.0) * ndl, 1.0);
}