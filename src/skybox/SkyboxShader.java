package skybox;

/**
 * Created by one on 7/31/16.
 */


import entities.Camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import shaders.ShaderProgram;
import toolBox.Maths;
import toolBox.Timer;

public class SkyboxShader extends ShaderProgram{

    private static final String VERTEX_FILE = "res/shaders/skyboxVertexShader.vert";
    private static final String FRAGMENT_FILE = "res/shaders/skyboxFragmentShader.frag";

    private static final float ROTATE_SPEED = 0.5f;

    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_fogColour;
    private int location_cubeMap;
    private int location_cubeMap2;
    private int location_blendFactor;

    private float rotation = 0;





    public SkyboxShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadProjectionMatrix(Matrix4f matrix){
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadFogColour(float r, float g, float b) {
        super.loadVector(location_fogColour, new Vector3f(r, g, b));
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f matrix = Maths.createViewMatrix(camera);
        // disable translation
        matrix.m30(0);
        matrix.m31(0);
        matrix.m32(0);
        rotation += ROTATE_SPEED * Timer.getElapsedTime();
        matrix.rotate(rotation, new Vector3f(0, 1, 0));

        super.loadMatrix(location_viewMatrix, matrix);
    }

    public void connectTextureUnits() {
        super.loadInt(location_cubeMap, 0);
        super.loadInt(location_cubeMap2, 1);
    }

    public void loadBlendFactor(float blend) {
        super.loadFloat(location_blendFactor, blend);
    }

    @Override
    protected void getAllUniformLocations() {
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_fogColour = super.getUniformLocation("fogColour");
        location_cubeMap = super.getUniformLocation("cubeMap");
        location_cubeMap2 = super.getUniformLocation("cubeMap2");
        location_blendFactor = super.getUniformLocation("blendFactor");

    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}