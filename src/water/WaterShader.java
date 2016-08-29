package water;

import lights.PointLight;
import org.joml.Matrix4f;
import shaders.ShaderProgram;
import entities.Camera;
import toolBox.Maths;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/water/waterVertex.vert";
	private final static String FRAGMENT_FILE = "res/shaders/water/waterFragment.frag";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_moveFactor;
	private int location_cameraPosition;
	private int location_normalMap;
	private int location_lightColour;
	private int location_lightPosition;
	private int location_depthMap;
	private int location_near;
	private int location_far;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_moveFactor = getUniformLocation("moveFactor");
		location_cameraPosition = getUniformLocation("cameraPosition");
		location_normalMap = getUniformLocation("normalMap");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPosition = getUniformLocation("lightPosition");
		location_depthMap = getUniformLocation("depthMap");
		location_near = getUniformLocation("near");
		location_far = getUniformLocation("far");

	}

	public void loadNearFarPlane(float near, float far) {
		super.loadFloat(location_near, near);
		super.loadFloat(location_far, far);
	}

	public void loadMoveFactor(float factor) {

		super.loadFloat(location_moveFactor, factor);
	}

	public void loadLight(PointLight pointLight) {
		super.loadVector(location_lightColour, pointLight.getColour());
		super.loadVector(location_lightPosition, pointLight.getPosition());
	}


	public void connectTextureUnits() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(location_cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
