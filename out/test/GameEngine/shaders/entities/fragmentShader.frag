#version 330 core

const int numberOfPointLights = 4;

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toCameraVector;
in float visibility;
in vec3 fromSpotLightVector;
in vec3 pass_worldPosition;

out vec4 out_Colour;

struct PointLight
{
    vec3 position;
    vec3 colour;
    vec3 attenuation;
};

struct DirectionalLight
{
    vec3 colour;
    vec3 direction;
    vec3 intensity;
};

struct SpotLight
{
    vec3 position;
    vec3 colour;
    vec3 direction;
    vec3 attenuation;
    float coneAngle;
};

uniform sampler2D textureSampler;

uniform PointLight pointLights[numberOfPointLights];

uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;
uniform mat4 viewMatrix;

uniform sampler2D normalSampler;
uniform float hasNormalMap;

uniform DirectionalLight directionalLight;
uniform SpotLight spotLight;

uniform sampler2D specularMap;
uniform float hasSpecularMap;

const float levels = 3.0;



void main(void) {

    vec3 toPointLightVectors[numberOfPointLights];

    for (int i = 0; i < numberOfPointLights; i++) {
        toPointLightVectors[i] = pointLights[i].position - pass_worldPosition; // v-after - v-before
    }

    vec4 dir = vec4(directionalLight.direction, 0);
    dir = dir * viewMatrix;
    vec3 direction = dir.xyz;


    vec3 newNormal = surfaceNormal;
    if (hasNormalMap == 1) {
        newNormal = texture(normalSampler, pass_textureCoordinates).rgb;
        newNormal = normalize(newNormal * 2 - 1);
    }

    vec3 unitNormal = normalize(newNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    // **** point light **** //
    for (int i = 0; i < numberOfPointLights; i++) {

        float distance = length(toPointLightVectors[i]);
        float attenuationFactor = pointLights[i].attenuation.x + (pointLights[i].attenuation.y * distance)+ (pointLights[i].attenuation.z * distance * distance);



        vec3 unitLightVector = normalize(toPointLightVectors[i]);
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        totalDiffuse = totalDiffuse + (brightness * pointLights[i].colour) / attenuationFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * pointLights[i].colour) / attenuationFactor;
    }
    // ********************* //


    // **** directional PointLight **** //
    vec3 unitLightVector = normalize(-directionalLight.direction);
    float nDotl = dot(unitNormal, unitLightVector);
    float brightness = max(nDotl, 0.0);
    vec3 reflectedLightDirection = reflect(directionalLight.direction, unitNormal);
    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    specularFactor = max(specularFactor, 0.0);
    float dampedFactor = pow(specularFactor, shineDamper);
    totalDiffuse = totalDiffuse + (brightness * directionalLight.colour);
    totalSpecular = totalSpecular + (dampedFactor * reflectivity * directionalLight.colour);
    // *************************** //


    // **** Spot PointLight **** //
    vec3 unitFromSpotLightVector = normalize(fromSpotLightVector);
    vec3 unitSpotLightDirection = normalize(spotLight.direction);
    float fDots = dot(unitFromSpotLightVector, unitSpotLightDirection);
    if (fDots > spotLight.coneAngle) {

        float distance = length(fromSpotLightVector);

        float attenuationFactor = spotLight.attenuation.x + (spotLight.attenuation.y * distance)+ (spotLight.attenuation.z * distance * distance);



        vec3 unitLightVector = -unitFromSpotLightVector;
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        vec3 lightDirection = unitFromSpotLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);



        totalDiffuse = totalDiffuse + (brightness * spotLight.colour) / attenuationFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * spotLight.colour) / attenuationFactor;


    }
    // *************************** //





    totalDiffuse = max(totalDiffuse, 0.2);


    vec4 textureColour = texture(textureSampler, pass_textureCoordinates);
    if (textureColour.a < 0.5) {
        discard;
    }

    if (hasSpecularMap == 1) {
        vec4 mapInfo = texture(specularMap, pass_textureCoordinates);
        totalSpecular *= mapInfo.r;
    }


    

    out_Colour = vec4(totalDiffuse, 1.0) * textureColour + vec4(totalSpecular, 1.0);
    out_Colour = mix(vec4(skyColour, 1.0), out_Colour, visibility);

}