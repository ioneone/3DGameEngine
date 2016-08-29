package lights;

import org.joml.Vector3f;

/**
 * Created by one on 8/9/16.
 */
public class SpotLight {

    private Vector3f position;
    private Vector3f colour;
    private Vector3f direction;
    private Vector3f attenuation;
    private float coneAngle;


    public SpotLight(Vector3f position, Vector3f colour, Vector3f direction, float coneAngle, Vector3f attenuation) {
        this.position = position;
        this.colour = colour;
        this.direction = direction;
        this.coneAngle = coneAngle;
        this.attenuation = attenuation;
    }

    public float getConeAngle() {
        return coneAngle;
    }

    public void setConeAngle(float coneAngle) {
        this.coneAngle = coneAngle;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public Vector3f getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3f attenuation) {
        this.attenuation = attenuation;
    }
}
