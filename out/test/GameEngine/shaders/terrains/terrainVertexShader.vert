#version 330 core

in vec3 position;
in vec2 textureCoordinates;
in vec3 normal;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 pass_worldPosition;
out vec3 toCameraVector;
out vec3 fromSpotLightVector;
out vec4 shadowCoords;
out float visibility;

struct SpotLight
{
    vec3 position;
    vec3 colour;
    vec3 direction;
    vec3 attenuation;
    float coneAngle;
};

struct Fog
{
    float density;
    float gradient;
};

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec4 plane;

uniform SpotLight spotLight;

uniform mat4 toShadowMapSpace;

uniform Fog fog;

const float shadowDistance = 300.0;
const float transitionDistance = 10.0;

void main(void) {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

    shadowCoords = toShadowMapSpace * worldPosition;

    vec4 positionRelativeToCamera = viewMatrix * worldPosition;

    gl_ClipDistance[0] = dot(worldPosition, plane);

    gl_Position = projectionMatrix * viewMatrix * worldPosition;
    pass_textureCoordinates = textureCoordinates;

    surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;

    pass_worldPosition = worldPosition.xyz;

    fromSpotLightVector = worldPosition.xyz - spotLight.position;

    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(positionRelativeToCamera.xyz);

    visibility = exp(-pow((distance*fog.density), fog.gradient));
    visibility = clamp(visibility, 0.0, 1.0);

    distance = distance - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    shadowCoords.w = clamp(1.0 - distance, 0.0, 1.0);



}