package terrains;

import entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.RawModel;
import textures.TexturedModel;
import toolBox.Maths;

import java.util.List;

/**
 * Created by one on 7/28/16.
 */
public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader) {

        this.shader = shader;
    }

    public void init(Matrix4f projectionMatrix) throws Exception {
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(List<Terrain> terrains, Matrix4f toShadowSpace) {

        shader.loadToShadowMapSpace(toShadowSpace);

        for (Terrain terrain:terrains) {
            prepareTerrain(terrain);

            loadModelMatrix(terrain);

            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); // render

            unbindTexturedModel();

        }

    }

    private void prepareTerrain(Terrain terrain) {

        RawModel rawModel = terrain.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID()); // if you want to do something with vao, you need to bind it
        GL20.glEnableVertexAttribArray(0); // position
        GL20.glEnableVertexAttribArray(1); // textureCoordinates
        GL20.glEnableVertexAttribArray(2); // normal

        // **** set specular light **** //
        shader.loadShineVariables(terrain.getShineDamper(), terrain.getReflectivity());
        // **************************** //

        bindTextures(terrain);

    }

    private void bindTextures(Terrain terrain) {
        TerrainTexturePack terrainTexturePack = terrain.getTerrainTexturePack();

        GL13.glActiveTexture(GL13.GL_TEXTURE0); // tell openGL which texture to draw
        // sampler2D uses textureBank0 by default
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTexturePack.getBackgroundTexture().getTextureID()); // bind our texture to it

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTexturePack.getrTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTexturePack.getgTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTexturePack.getbTexture().getTextureID());

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID());

    }

    private void unbindTexturedModel() {

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

    }

    private void loadModelMatrix(Terrain terrain) {

        // **** set transformationMatrix **** //
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()),
                0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
        // ********************************** //

    }



}
