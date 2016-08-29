package postProcessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Created by one on 8/14/16.
 */
public class ContrastChanger {

    private ImageRenderer imageRenderer;
    private ContrastShader contrastShader;

    public ContrastChanger() {
        contrastShader = new ContrastShader();
        imageRenderer = new ImageRenderer();
    }

    public void render(int texture) {
        contrastShader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        imageRenderer.renderQuad();
        contrastShader.stop();
    }

    public void cleanUp() {
        imageRenderer.cleanUp();
        contrastShader.cleanUp();
    }

}
