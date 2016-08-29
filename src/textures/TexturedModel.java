package textures;

import renderEngine.RawModel;

/**
 * Created by one on 7/24/16.
 */
public class TexturedModel {

    private RawModel rawModel;
    private Texture texture;
    private Texture normalMap;
    private Texture specularMap;

    private float shineDamper = 1;
    private float reflectivity = 0;
    private boolean useFakeLighting = false;

    public TexturedModel(RawModel rawModel, Texture texture) {
        this.rawModel = rawModel;
        this.texture = texture;
    }

    public boolean hasSpecularMap() {
        return this.specularMap != null;
    }

    public void setSpecularMap(Texture specularMap) {
        this.specularMap = specularMap;
    }

    public Texture getSpecularMap() {
        return specularMap;
    }

    public boolean hasNormalMap() {
        return this.normalMap != null;
    }

    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public TexturedModel(RawModel rawModel) {
        this.rawModel = rawModel;
    };

    public RawModel getRawModel() {
        return rawModel;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getShineDamper() {
        return shineDamper;
    }

    public void setShineDamper(float shineDamper) {
        this.shineDamper = shineDamper;
    }

    public float getReflectivity() {
        return reflectivity;
    }

    public void setReflectivity(float reflectivity) {
        this.reflectivity = reflectivity;
    }

    public boolean isUseFakeLighting() {
        return useFakeLighting;
    }

    public void setUseFakeLighting(boolean useFakeLighting) {
        this.useFakeLighting = useFakeLighting;
    }


}
