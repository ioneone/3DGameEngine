package entities;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import textures.TexturedModel;

/**
 * Created by one on 7/24/16.
 */
public class Entity {

    private TexturedModel[] texturedModels;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;
    private int textureIndex = 0;

    public Entity(TexturedModel texturedModel, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.texturedModels = new TexturedModel[]{texturedModel};
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public Entity(TexturedModel texturedModel, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.textureIndex = textureIndex;
        this.texturedModels = new TexturedModel[]{texturedModel};
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public Entity(TexturedModel[] meshes) {
        this.texturedModels = meshes;
    }

    public float getTextureXOffset() {
        int column = textureIndex % texturedModels[0].getTexture().getNumRows();
        return (float)column / (float)texturedModels[0].getTexture().getNumRows();
    }

    public float getTextureYOffset() {
        int row = textureIndex / texturedModels[0].getTexture().getNumRows();
        return (float)row / (float)texturedModels[0].getTexture().getNumRows();
    }

    public void increasePostion(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public TexturedModel getTexturedModel() {
        return texturedModels[0];
    }

    public TexturedModel[] getTexturedModels() {
        return texturedModels;
    }

    public void setTexturedModel(TexturedModel texturedModel) {
        this.texturedModels[0] = texturedModel;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }


}
