package guis;

import org.joml.Matrix4f;
import shaders.ShaderProgram;

/**
 * Created by one on 7/30/16.
 */
public class GUIShader extends ShaderProgram {

    private static final String VERTEX_FILE = "res/shaders/guis/GUIVertexShader.vert";
    private static final String FRAGMENT_FILE = "res/shaders/guis/GUIFragmentShader.frag";

    private int location_transformationMatrix;

    public GUIShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    public void loadTransformation(Matrix4f matrix){
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

}
