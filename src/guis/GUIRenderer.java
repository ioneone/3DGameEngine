package guis;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import loaders.Loader;
import renderEngine.RawModel;
import toolBox.Maths;

import java.util.List;

/**
 * Created by one on 7/30/16.
 */
public class GUIRenderer {

    private RawModel quad;
    private GUIShader guiShader;


    public GUIRenderer() {

    }

    public void init(Loader loader) {
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = loader.loadToVAO(positions, 2);
        guiShader = new GUIShader();
    }

    public void render(List<GUITexture> guis) {
        guiShader.start();

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        // **** enable transparency **** //
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for (GUITexture gui:guis) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, gui.getTextureID());
            Matrix4f matrix = Maths.createTransformationMatrix(gui.getPosition(), gui.getScale());
            guiShader.loadTransformation(matrix);
            GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        guiShader.stop();
    }

    public void cleanUp() {
        guiShader.cleanUp();
    }
}
