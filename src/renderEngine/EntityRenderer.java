package renderEngine;

import entities.Entity;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.StaticShader;
import textures.Texture;
import textures.TexturedModel;
import toolBox.Maths;

import java.util.List;
import java.util.Map;

/**
 * Created by one on 7/23/16.
 */
public class EntityRenderer {

    private StaticShader shader;

    public EntityRenderer(StaticShader shader) {

        this.shader = shader;

    }

    public void render(Map<TexturedModel, List<Entity>> entities) {

        for (TexturedModel model:entities.keySet()) {
            prepareTexturedModel(model);
            List<Entity> batch = entities.get(model);

            for (Entity entity:batch) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); // render
            }

            unbindTexturedModel();

        }

    }


    private void prepareTexturedModel(TexturedModel model) {

        RawModel rawModel = model.getRawModel();
        GL30.glBindVertexArray(rawModel.getVaoID()); // if you want to do something with vao, you need to bind it. specify which VAO ID we want to use.
        GL20.glEnableVertexAttribArray(0); // position
        GL20.glEnableVertexAttribArray(1); // textureCoordinates
        GL20.glEnableVertexAttribArray(2); // normal

        Texture texture = model.getTexture();

        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }

        // **** set fake lighting **** //
        shader.loadFakeLightingVariable(model.isUseFakeLighting());
        // *************************** //

        // **** set specular light **** //
        shader.loadShineVariables(model.getShineDamper(), model.getShineDamper());
        // *************************** //

        // **** set textureAtlas **** //
        shader.loadNumberOfRows(texture.getNumRows());
        // ************************** //
        GL13.glActiveTexture(GL13.GL_TEXTURE0); // tell openGL which texture to draw
        // sampler2D uses textureBank0 by default
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId()); // bind our texture to it

        shader.loadNormalSampler();

        if (model.hasNormalMap()) {
            shader.loadHasNormalMap(true);
            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getNormalMap().getId());
        } else {
            shader.loadHasNormalMap(false);
        }

        if (model.hasSpecularMap()) {
            shader.loadHasSpecularMap(true);
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getSpecularMap().getId());
        } else {
            shader.loadHasSpecularMap(false);
        }

    }

    private void unbindTexturedModel() {

        MasterRenderer.enableCulling();

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1); // texture
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);

    }

    private void prepareInstance(Entity entity) {

        // **** set transformationMatrix **** //
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(),
                entity.getRotX(), entity.getRotY(),entity.getRotZ(), entity.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        // ********************************** //

        // **** set texture atlas offset **** //
        shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
        // ********************************** //

    }

    public void init(Matrix4f projectionMatrix) throws Exception {

        // **** projectionMatrix should be made just once for each entity because it's not gonna change (window size is fixed in our game)**** //
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        // ******************************************************************************************************************* //


    }


    public void cleanUp() {
        shader.cleanUp();
    }






}
