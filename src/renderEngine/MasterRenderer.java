package renderEngine;

import entities.*;

import lights.DirectionalLight;
import lights.PointLight;
import lights.SpotLight;
import loaders.Loader;


import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import shaders.StaticShader;
import shadows.ShadowMapMasterRenderer;
import terrains.Terrain;
import terrains.TerrainRenderer;
import terrains.TerrainShader;
import textures.TexturedModel;
import weathers.Fog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClearColor;

/**
 * Created by one on 7/26/16.
 */
public class MasterRenderer {

    public static final float FOV = 70; // Field Of View
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000; // how far you can see

    public static final float RED = 0.5f;
    public static final float GREEN = 0.5f;
    public static final float BLUE = 0.5f;

    private StaticShader shader;
    private EntityRenderer renderer;

    private TerrainShader terrainShader;
    private TerrainRenderer terrainRenderer;

    private ShadowMapMasterRenderer shadowMapMasterRenderer;

    //private SkyboxRenderer skyboxRenderer;

    private Matrix4f projectionMatrix;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<TexturedModel, List<Entity>>();

    private List<Terrain> terrains = new ArrayList<Terrain>();

    public MasterRenderer() {

    }

    public void init(Window window, Loader loader, Camera camera) throws Exception {

        shader = new StaticShader();
        renderer = new EntityRenderer(shader);

        terrainShader = new TerrainShader();
        terrainRenderer = new TerrainRenderer(terrainShader);


        // **** projectionMatrix should be made just once for each entity because it's not gonna change (window size is fixed in our game)**** //
        projectionMatrix = createProjectionMatrix(window.getWidth(), window.getHeight());
        // ******************************************************************************************************************* //

        // **** Disable inside(back-face) of the object being rendered (memory management) **** //
        enableCulling();
        // ************************************************************************* //

        //skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        renderer.init(projectionMatrix);
        terrainRenderer.init(projectionMatrix);
        shadowMapMasterRenderer = new ShadowMapMasterRenderer(camera);

    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);

    }

    public void prepare() { // this method will be called every frame
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        glClearColor(RED, GREEN, BLUE, 0.0f);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL_TEXTURE_2D, getShadowMapTexture());

    }

    public void render(List<PointLight> pointLights, Camera camera, Vector4f clipPlane, DirectionalLight directionalLight, SpotLight spotLight, Fog fog) {

        prepare();

        shader.start();
        shader.loadDirectionalLight(directionalLight);
        //shader.loadSpotLight(spotLight);
        shader.loadClipPlane(clipPlane);
        // **** set PointLight **** //
        shader.loadPointLights(pointLights);
        // ******************* //
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadFog(fog);
        // **** set viewMatrix **** //
        shader.loadViewMatrix(camera);
        // ************************ //
        renderer.render(entities);
        shader.stop();


        terrainShader.start();
        //terrainShader.loadSpotLight(spotLight);
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadFog(fog);
        terrainShader.loadDirectionalLight(directionalLight);
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadLights(pointLights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();

        //skyboxRenderer.render(camera, RED, GREEN, BLUE);

        terrains.clear();
        entities.clear();

    }

    // **** a method for loading terrain **** //
    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }
    // ************************************** //


    // **** store entities to HashMap **** //
    public void processEntity(Entity entity) {
        TexturedModel model = entity.getTexturedModel();
        List<Entity> batch = entities.get(model);
        if (batch != null) {

            batch.add(entity);

        } else {

            List<Entity> newBatch = new ArrayList<Entity>();
            newBatch.add(entity);
            entities.put(model, newBatch);

        }

    }
    // *********************************** //



    public void cleanUp() {

        shader.cleanUp();
        terrainShader.cleanUp();
        shadowMapMasterRenderer.cleanUp();

    }

    public void renderShadowMap(List<Entity> entityList, PointLight sun) {
        for (Entity entity:entityList) {
            processEntity(entity);
        }

        shadowMapMasterRenderer.render(entities, sun);
        entities.clear();
    }

    public int getShadowMapTexture() {
        return shadowMapMasterRenderer.getShadowMap();
    }

    private Matrix4f createProjectionMatrix(float width, float height) {

        Matrix4f projectionMatrix = new Matrix4f();
        float aspectRatio = width / height;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00(x_scale);
        projectionMatrix.m11(y_scale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
        projectionMatrix.m33(0);
        return projectionMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public void renderScene(List<Entity> entities, Terrain[][] terrains, List<PointLight> pointLights, Camera camera, Vector4f clipPlane, DirectionalLight directionalLight, SpotLight spotLight, Fog fog) {
        for (int i = 0; i < terrains.length; i++) {
            Terrain[] terrainsRow = terrains[i];
            for (int j = 0; j < terrainsRow.length; j++) {
                Terrain terrain = terrainsRow[j];
                processTerrain(terrain);
            }
        }
        for (Entity entity:entities) {
            processEntity(entity);
        }

        render(pointLights, camera, clipPlane, directionalLight, spotLight, fog);

    }

}