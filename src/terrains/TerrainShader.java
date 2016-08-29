package terrains;

import entities.Camera;
import lights.DirectionalLight;
import lights.PointLight;
import lights.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import shaders.ShaderProgram;
import toolBox.Maths;
import weathers.Fog;

import java.util.List;

/**
 * Created by one on 7/28/16.
 */
public class TerrainShader extends ShaderProgram {

    private static final int MAX_POINT_LIGHTS = 4;

    private static final String VERTEX_FILE = "res/shaders/terrains/terrainVertexShader.vert";
    private static final String FRAGMENT_FILE = "res/shaders/terrains/terrainFragmentShader.frag";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_pointLight_positions;
    private int[] location_pointLight_colours;
    private int[] location_pointLight_attenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_backgroundTexture;
    private int location_rTexture;
    private int location_gTexture;
    private int location_bTexture;
    private int location_blendMap;
    private int location_plane;
    private int location_directionalLight_colour;
    private int location_directionalLight_direction;
    private int location_directionalLight_intensity;
    private int location_spotLight_position;
    private int location_spotLight_colour;
    private int location_spotLight_direction;
    private int location_spotLight_attenuation;
    private int location_spotLight_coneAngle;
    private int location_toShadowMapSpace;
    private int location_shadowMap;
    private int location_skyColour;
    private int location_fog_density;
    private int location_fog_gradient;



    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() { // connect class StaticShader and input of vertexShader.vs
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() { // get uniform locations and store them (connecting java code and shader code)
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_rTexture = super.getUniformLocation("rTexture");
        location_gTexture = super.getUniformLocation("gTexture");
        location_bTexture = super.getUniformLocation("bTexture");
        location_blendMap = super.getUniformLocation("blendMap");
        location_plane = super.getUniformLocation("plane");

        location_pointLight_positions = new int[MAX_POINT_LIGHTS];
        location_pointLight_colours = new int[MAX_POINT_LIGHTS];
        location_pointLight_attenuation = new int[MAX_POINT_LIGHTS];
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            location_pointLight_positions[i] = super.getUniformLocation("pointLights[" + i + "].position");
            location_pointLight_colours[i] = super.getUniformLocation("pointLights[" + i + "].colour");
            location_pointLight_attenuation[i] = super.getUniformLocation("pointLights[" + i + "].attenuation");
        }

        location_directionalLight_colour = super.getUniformLocation("directionalLight.colour");
        location_directionalLight_direction = super.getUniformLocation("directionalLight.direction");
        location_directionalLight_intensity = super.getUniformLocation("directionalLight.intensity");

        location_spotLight_position = super.getUniformLocation("spotLight.position");
        location_spotLight_colour = super.getUniformLocation("spotLight.colour");
        location_spotLight_direction = super.getUniformLocation("spotLight.direction");
        location_spotLight_attenuation = super.getUniformLocation("spotLight.attenuation");
        location_spotLight_coneAngle = super.getUniformLocation("spotLight.coneAngle");

        location_toShadowMapSpace = super.getUniformLocation("toShadowMapSpace");
        location_shadowMap = super.getUniformLocation("shadowMap");

        location_skyColour = super.getUniformLocation("skyColour");
        location_fog_density = super.getUniformLocation("fog.density");
        location_fog_gradient = super.getUniformLocation("fog.gradient");

    }

    // **** Methods to load data in each uniform before being passed in to shader **** //

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector(location_skyColour, new Vector3f(r, g, b));
    }

    public void loadFog(Fog fog) {
        super.loadFloat(location_fog_density, fog.getDensity());
        super.loadFloat(location_fog_gradient, fog.getGradient());
    }

    public void loadToShadowMapSpace(Matrix4f matrix) {
        super.loadMatrix(location_toShadowMapSpace, matrix);
    }

    public void loadSpotLight(SpotLight light) {
        super.loadVector(location_spotLight_position, light.getPosition());
        super.loadVector(location_spotLight_colour, light.getColour());
        super.loadVector(location_spotLight_direction, light.getDirection());
        super.loadVector(location_spotLight_attenuation, light.getAttenuation());
        super.loadFloat(location_spotLight_coneAngle, light.getConeAngle());
    }

    public void loadDirectionalLight(DirectionalLight light) {
        super.loadVector(location_directionalLight_colour, light.getColour());
        super.loadVector(location_directionalLight_direction, light.getDirection());
        super.loadFloat(location_directionalLight_intensity, light.getIntensity());
    }


    public void loadClipPlane(Vector4f plane) {
        super.loadVector(location_plane, plane);
    }

    public void connectTextureUnits() {

        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_rTexture, 1);
        super.loadInt(location_gTexture, 2);
        super.loadInt(location_bTexture, 3);
        super.loadInt(location_blendMap, 4);
        super.loadInt(location_shadowMap, 5);

    }
    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadLights(List<PointLight> pointLights) {
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            if (i < pointLights.size()) {
                super.loadVector(location_pointLight_positions[i], pointLights.get(i).getPosition());
                super.loadVector(location_pointLight_colours[i], pointLights.get(i).getColour());
                super.loadVector(location_pointLight_attenuation[i], pointLights.get(i).getAttenuation());
            } else {
                super.loadVector(location_pointLight_positions[i], new Vector3f(0, 0, 0));
                super.loadVector(location_pointLight_colours[i], new Vector3f(0, 0, 0));
                super.loadVector(location_pointLight_attenuation[i], new Vector3f(1, 0, 0));
            }
        }
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    // ******************************************************************************* //



}
