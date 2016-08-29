package shaders;

import entities.Camera;
import lights.DirectionalLight;
import lights.PointLight;
import lights.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import toolBox.Maths;
import weathers.Fog;

import java.util.List;

/**
 * Created by one on 7/23/16.
 */
public class StaticShader extends ShaderProgram {

    private static final int MAX_POINT_LIGHTS = 4;

    private static final String VERTEX_FILE = "res/shaders/entities/vertexShader.vert";
    private static final String FRAGMENT_FILE = "res/shaders/entities/fragmentShader.frag";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_pointLight_positions;
    private int[] location_pointLight_colours;
    private int[] location_pointLight_attenuation;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_useFakeLighting;
    private int location_skyColour;
    private int location_numberOfRows;
    private int location_offset;
    private int location_plane;
    private int location_hasNormalMap;
    private int location_normalSampler;
    private int location_directionalLight_colour;
    private int location_directionalLight_direction;
    private int location_directionalLight_intensity;
    private int location_spotLight_position;
    private int location_spotLight_colour;
    private int location_spotLight_direction;
    private int location_spotLight_attenuation;
    private int location_spotLight_coneAngle;
    private int location_specularMap;
    private int location_hasSpecularMap;
    private int location_fog_density;
    private int location_fog_gradient;


    public StaticShader() {
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
        location_useFakeLighting = super.getUniformLocation("useFakeLighting");
        location_skyColour = super.getUniformLocation("skyColour");
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_offset = super.getUniformLocation("offset");
        location_plane = super.getUniformLocation("plane");

        // **** PointLight **** //
        location_pointLight_positions = new int[MAX_POINT_LIGHTS];
        location_pointLight_colours = new int[MAX_POINT_LIGHTS];
        location_pointLight_attenuation = new int[MAX_POINT_LIGHTS];
        for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
            location_pointLight_positions[i] = super.getUniformLocation("pointLights[" + i + "].position");
            location_pointLight_colours[i] = super.getUniformLocation("pointLights[" + i + "].colour");
            location_pointLight_attenuation[i] = super.getUniformLocation("pointLights[" + i + "].attenuation");
        }
        // ******************** //

        location_hasNormalMap = super.getUniformLocation("hasNormalMap");
        location_normalSampler = super.getUniformLocation("normalSampler");

        location_directionalLight_colour = super.getUniformLocation("directionalLight.colour");
        location_directionalLight_direction = super.getUniformLocation("directionalLight.direction");
        location_directionalLight_intensity = super.getUniformLocation("directionalLight.intensity");

        location_spotLight_position = super.getUniformLocation("spotLight.position");
        location_spotLight_colour = super.getUniformLocation("spotLight.colour");
        location_spotLight_direction = super.getUniformLocation("spotLight.direction");
        location_spotLight_attenuation = super.getUniformLocation("spotLight.attenuation");
        location_spotLight_coneAngle = super.getUniformLocation("spotLight.coneAngle");

        location_specularMap = super.getUniformLocation("specularMap");
        location_hasSpecularMap = super.getUniformLocation("hasSpecularMap");

        location_fog_density = super.getUniformLocation("fog.density");
        location_fog_gradient = super.getUniformLocation("fog.gradient");


    }

    // **** Methods to load data in each uniform before being passed in to shader **** //

    public void loadFog(Fog fog) {
        super.loadFloat(location_fog_density, fog.getDensity());
        super.loadFloat(location_fog_gradient, fog.getGradient());
    }

    public void loadSpecularMap() {
        super.loadInt(location_specularMap, 2);
    }

    public void loadHasSpecularMap(boolean hasSpecularMap) {
        super.loadBoolean(location_hasSpecularMap, hasSpecularMap);
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

    public void loadNormalSampler() {
        super.loadInt(location_normalSampler, 1);
    }

    public void loadHasNormalMap(boolean hasNormalMap) {
        super.loadBoolean(location_hasNormalMap, hasNormalMap);
    }

    public void loadClipPlane(Vector4f plane) {
        super.loadVector(location_plane, plane);
    }

    public void loadNumberOfRows(int numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y) {

        super.load2DVector(location_offset, new Vector2f(x, y));
    }

    public void loadSkyColour(float r, float g, float b) {

        super.loadVector(location_skyColour, new Vector3f(r, g, b));

    }

    public void loadFakeLightingVariable(boolean useFake) {
        super.loadBoolean(location_useFakeLighting, useFake);

    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadPointLights(List<PointLight> pointLights) {

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
