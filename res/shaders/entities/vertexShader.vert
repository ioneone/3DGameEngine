#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 textureCoordinates;
layout (location=2) in vec3 normal;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toCameraVector;
out float visibility;
out vec3 fromSpotLightVector;
out vec3 pass_worldPosition;


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

uniform float useFakeLighting;

// **** texture atlas **** //
uniform float numberOfRows;
uniform vec2 offset;
// *********************** //

uniform SpotLight spotLight;

uniform Fog fog;

uniform vec4 plane;

void main(void) {

    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

    gl_ClipDistance[0] = dot(worldPosition, plane);

    vec4 positionRelativeToCamera = viewMatrix * worldPosition;

    gl_Position = projectionMatrix * positionRelativeToCamera;
    pass_textureCoordinates = (textureCoordinates / numberOfRows) + offset;

    vec3 actualNormal = normal;
    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1.0, 0.0);
    }

    surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;

    pass_worldPosition = worldPosition.xyz;

    fromSpotLightVector = worldPosition.xyz - spotLight.position;


    toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;

    float distance = length(positionRelativeToCamera.xyz);
    visibility = exp(-pow((distance*fog.density), fog.gradient));
    visibility = clamp(visibility, 0.0, 1.0);

}