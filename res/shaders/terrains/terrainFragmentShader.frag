#version 330 core

const int numberOfPointLights = 4;

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toCameraVector;
in vec3 fromSpotLightVector;
in vec4 shadowCoords;
in float visibility;
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

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform float shineDamper;
uniform float reflectivity;

uniform PointLight pointLights[numberOfPointLights];

uniform DirectionalLight directionalLight;
uniform SpotLight spotLight;

uniform vec3 skyColour;

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

void main(void) {

    vec3 toPointLightVectors[numberOfPointLights];

    for (int i = 0; i < numberOfPointLights; i++) {
        toPointLightVectors[i] = pointLights[i].position - pass_worldPosition; // v-after - v-before
    }

    float mapSize = 4096.0;
    float texelSize = 1.0 / mapSize;
    float total = 0.0;
    for (int x = -pcfCount; x <= pcfCount; x++) {
        for (int y = -pcfCount; y <= pcfCount; y++) {
                float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
                if (shadowCoords.z > objectNearestLight + 0.002) {
                        total += 1.0;
                    }
            }
    }

    total /= totalTexels;

    float lightFactor = 1.0 - (total * shadowCoords.w);

    // **** terrain texture **** //
    vec4 blendMapColour = texture(blendMap, pass_textureCoordinates);
    float backgroundTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b); // render background where it is black
    vec2 tiledCoordinates = pass_textureCoordinates * 40.0;
    vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoordinates) * backgroundTextureAmount;
    vec4 rTextureColour = texture(rTexture, tiledCoordinates) * blendMapColour.r;
    vec4 gTextureColour = texture(gTexture, tiledCoordinates) * blendMapColour.g;
    vec4 bTextureColour = texture(bTexture, tiledCoordinates) * blendMapColour.b;
    vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;
    // ************************ //


    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    // **** point lights **** //
    for (int i = 0; i < numberOfPointLights; i++) {

        float distance = length(toPointLightVectors[i]);
        float attenuationFactor = pointLights[i].attenuation.x + (pointLights[i].attenuation.y * distance) + (pointLights[i].attenuation.z * distance * distance);


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

    // **** directional light **** //
    vec3 unitLightVector = normalize(-directionalLight.direction);
    float nDotl = dot(unitNormal, unitLightVector);
    float brightness = max(nDotl, 0.0);
    vec3 reflectedLightDirection = reflect(directionalLight.direction, unitNormal);
    float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
    specularFactor = max(specularFactor, 0.0);
    float dampedFactor = pow(specularFactor, shineDamper);
    totalDiffuse = totalDiffuse + (brightness * directionalLight.colour);
    totalSpecular = totalSpecular + (dampedFactor * reflectivity * directionalLight.colour);
    // ************************** //

    // **** spot light **** //
    vec3 unitFromSpotLightVector = normalize(fromSpotLightVector);
    vec3 unitSpotLightDirection = normalize(spotLight.direction);
    float fDots = dot(unitFromSpotLightVector, unitSpotLightDirection);
    if (fDots > spotLight.coneAngle) {
        float distance = length(fromSpotLightVector);
        float attenuationFactor = spotLight.attenuation.x + (spotLight.attenuation.y * distance)+ (spotLight.attenuation.z * distance * distance);
        float brightness = 0.2;
        float dampedFactor = 0.2;
        totalDiffuse = totalDiffuse + (brightness * spotLight.colour) / attenuationFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * spotLight.colour) / attenuationFactor;
    }
    // ********************* //

    totalDiffuse = max(totalDiffuse * lightFactor, 0.2);

    out_Colour = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
    out_Colour = mix(vec4(skyColour, 1.0), out_Colour, visibility);

}