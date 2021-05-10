#version 460

in vec3 position;
in vec3 normal;
in vec2 uv;

uniform sampler2D diffuseTex;
uniform mat3 normalMatrix;
uniform vec2 normalScale;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform vec3 cameraPosition;

uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D roughnessTexture;
uniform sampler2D displacementTexture;
uniform sampler2D metalnessTexture;
out vec4 fragColor;

vec3 lightPosition = vec3(1.0, 1.0, 1.0);
const vec3 lightColor = vec3(1.0, 1.0, 1.0);

// Normal Mapping Without Precomputed Tangents
// http://www.thetenthplanet.de/archives/1180
vec3 perturbNormal2Arb( vec3 eye_pos, vec3 surf_norm, vec3 mapN, float faceDirection ) {
  vec3 q0 = vec3( dFdx( eye_pos.x ), dFdx( eye_pos.y ), dFdx( eye_pos.z ) );
  vec3 q1 = vec3( dFdy( eye_pos.x ), dFdy( eye_pos.y ), dFdy( eye_pos.z ) );
  vec2 st0 = dFdx( uv.st );
  vec2 st1 = dFdy( uv.st );
  vec3 N = surf_norm; // normalized
  vec3 q1perp = cross( q1, N );
  vec3 q0perp = cross( N, q0 );
  vec3 T = q1perp * st0.x + q0perp * st1.x;
  vec3 B = q1perp * st0.y + q0perp * st1.y;
  float det = max( dot( T, T ), dot( B, B ) );
  float scale = ( det == 0.0 ) ? 0.0 : faceDirection * inversesqrt( det );
  return normalize( T * ( mapN.x * scale ) + B * ( mapN.y * scale ) + N * mapN.z );
}

const float PI = 3.14159265359;
// ----------------------------------------------------------------------------
float DistributionGGX(vec3 N, vec3 H, float roughness)
{
  float a = roughness*roughness;
  float a2 = a*a;
  float NdotH = max(dot(N, H), 0.0);
  float NdotH2 = NdotH*NdotH;

  float nom   = a2;
  float denom = (NdotH2 * (a2 - 1.0) + 1.0);
  denom = PI * denom * denom;

  return nom / max(denom, 0.001);
}

float GeometrySchlickGGX(float NdotV, float roughness)
{
  float r = (roughness + 1.0);
  float k = (r*r) / 8.0;

  float nom   = NdotV;
  float denom = NdotV * (1.0 - k) + k;

  return nom / denom;
}

float GeometrySmith(vec3 N, vec3 V, vec3 L, float roughness)
{
  float NdotV = max(dot(N, V), 0.0);
  float NdotL = max(dot(N, L), 0.0);
  float ggx2 = GeometrySchlickGGX(NdotV, roughness);
  float ggx1 = GeometrySchlickGGX(NdotL, roughness);

  return ggx1 * ggx2;
}

vec3 fresnelSchlickRoughness(float cosTheta, vec3 F0, float roughness)
{
  return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
}

float faceDirection = gl_FrontFacing ? 1.0 : - 1.0;

void main() {
  float disp = texture(displacementTexture, uv).r;
  vec3 p = vec3(position);
  p.z *= disp;
  vec3 mapN = texture( normalTexture, uv ).xyz * 2.0 - 1.0;
  mapN.xy *= normalScale;

  vec3 NN = normalize(normal);
  vec3 V = normalize(-cameraPosition);
  vec3 N = perturbNormal2Arb(position, NN, mapN, faceDirection);

  vec3 albedo = texture(diffuseTexture, uv).xyz;
  float roughness = texture(roughnessTexture, uv).r;
  float metallic = texture(metalnessTexture, uv).r;

  vec3 F0 = vec3(0.04);
  F0 = mix(F0, albedo, metallic);

  vec3 Lo = vec3(0.0);
  vec3 L = normalize(lightPosition - p);
  vec3 H = normalize(V + L);
  float distance = length(lightPosition - p);
  float attenuation = 1.0 / (distance * distance);
  vec3 radiance = lightColor * attenuation;

  // Cook-Torrance BRDF
  float NDF = DistributionGGX(N, H, roughness);
  float G   = GeometrySmith(N, V, L, roughness);
  vec3 F    = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);

  vec3 nominator    = NDF * G * F;
  float denominator = 4 * max(dot(N, V), 0.0) * max(dot(N, L), 0.0);
  vec3 specular = nominator / max(denominator, 0.001);

  float kS = 0;
  float kD = (1 - kS) * (1 - metallic);
  float NdotL = max(dot(N, L), 0.0);
  Lo += kD * (albedo / PI) * radiance * NdotL;
  vec3 ambient = vec3(0.03) * albedo;
  vec3 color = Lo * 10.0;

  // HDR tonemapping
 color = color / (color + vec3(1.0));
  // gamma correct
  color = pow(color, vec3(1.0/2.2));

  fragColor = vec4(albedo * NdotL, 1.0);
}