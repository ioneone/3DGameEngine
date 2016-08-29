package lights;

import org.joml.Vector3f;

/**
 * Created by one on 8/9/16.
 */
public class DirectionalLight {

    private Vector3f colour;
    private Vector3f direction;
    private float intensity;
    private OrthoCoords orthoCoords;
    private float shadowPosMult;


    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        this.colour = colour;
        this.direction = direction;
        this.intensity = intensity;
        this.orthoCoords = new OrthoCoords();
        this.shadowPosMult = 1;

    }

    public DirectionalLight(DirectionalLight light) {
        this(new Vector3f(light.getColour()), new Vector3f(light.getDirection()), light.getIntensity());
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public OrthoCoords getOrthoCoords() {
        return orthoCoords;
    }

    public void setOrthoCoords(float left, float right, float bottom, float top, float near, float far) {
        orthoCoords.left = left;
        orthoCoords.right = right;
        orthoCoords.bottom = bottom;
        orthoCoords.top = top;
        orthoCoords.near = near;
        orthoCoords.far = far;
    }

    public float getShadowPosMult() {
        return shadowPosMult;
    }

    public void setShadowPosMult(float shadowPosMult) {
        this.shadowPosMult = shadowPosMult;
    }

    public static class OrthoCoords {

        public float left;

        public float right;

        public float bottom;

        public float top;

        public float near;

        public float far;
    }
}
