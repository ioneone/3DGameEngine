package guis;

import org.joml.Vector2f;

/**
 * Created by one on 7/30/16.
 */
public class GUITexture {

    private int textureID;
    private Vector2f position;
    private Vector2f scale;

    public GUITexture(int textureId, Vector2f position, Vector2f scale) {
        this.textureID = textureId;
        this.position = position;
        this.scale = scale;
    }

    public int getTextureID() {
        return textureID;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }
}
